package com.shop.admin.coupon.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shop.admin.common.api.PageResult;
import com.shop.admin.common.constant.CommonConstants;
import com.shop.admin.common.enums.CommonStatus;
import com.shop.admin.common.enums.CouponType;
import com.shop.admin.common.exception.BusinessException;
import com.shop.admin.coupon.dto.CouponCreateRequest;
import com.shop.admin.coupon.dto.CouponQueryRequest;
import com.shop.admin.coupon.dto.CouponStatusRequest;
import com.shop.admin.coupon.dto.CouponUpdateRequest;
import com.shop.admin.coupon.entity.CouponDO;
import com.shop.admin.coupon.mapper.CouponMapper;
import com.shop.admin.coupon.service.CouponService;
import com.shop.admin.coupon.vo.CouponVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * 优惠券服务实现。
 *
 * @author shop-admin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {

    private static final BigDecimal MAX_DISCOUNT_RATE = new BigDecimal("9.99");

    private final CouponMapper couponMapper;

    @Override
    public PageResult<CouponVO> pageCoupons(CouponQueryRequest request) {
        validateCouponType(request.getCouponType());
        validateStatus(request.getStatus());
        int pageNum = request.getPageNum() == null ? CommonConstants.DEFAULT_PAGE_NUM : request.getPageNum();
        int pageSize = request.getPageSize() == null ? CommonConstants.DEFAULT_PAGE_SIZE : request.getPageSize();
        LambdaQueryWrapper<CouponDO> wrapper = Wrappers.<CouponDO>lambdaQuery();
        String keyword = trimToNull(request.getKeyword());
        if (keyword != null) {
            wrapper.and(query -> query.like(CouponDO::getName, keyword)
                    .or()
                    .like(CouponDO::getCouponCode, keyword));
        }
        if (StringUtils.hasText(request.getCouponType())) {
            wrapper.eq(CouponDO::getCouponType, request.getCouponType());
        }
        if (request.getStatus() != null) {
            wrapper.eq(CouponDO::getStatus, request.getStatus());
        }
        wrapper.orderByDesc(CouponDO::getCreateTime);
        Page<CouponDO> couponPage = couponMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        List<CouponVO> records = couponPage.getRecords().stream()
                .map(CouponVO::from)
                .toList();
        return PageResult.of(couponPage.getTotal(), couponPage.getCurrent(), couponPage.getSize(), records);
    }

    @Override
    public CouponVO getCoupon(Long id) {
        return CouponVO.from(getRequiredCoupon(id));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createCoupon(CouponCreateRequest request) {
        CouponType couponType = parseCouponType(request.getCouponType());
        validateCouponRules(couponType, request.getDiscountValue(), request.getThresholdAmount(),
                request.getValidStartTime(), request.getValidEndTime());
        validateStatus(request.getStatus());

        String couponCode = trimToNull(request.getCouponCode());
        if (couponCode == null) {
            couponCode = generateCouponCode();
        }
        if (couponMapper.selectCount(Wrappers.<CouponDO>lambdaQuery()
                .eq(CouponDO::getCouponCode, couponCode)) > 0) {
            throw new BusinessException(409, "优惠券编码已存在");
        }

        CouponDO couponDO = new CouponDO();
        couponDO.setCouponCode(couponCode);
        couponDO.setName(request.getName().trim());
        couponDO.setCouponType(couponType.getCode());
        couponDO.setDiscountValue(request.getDiscountValue());
        couponDO.setThresholdAmount(request.getThresholdAmount() == null
                ? BigDecimal.ZERO : request.getThresholdAmount());
        couponDO.setTotalCount(request.getTotalCount());
        couponDO.setReceivedCount(0);
        couponDO.setUsedCount(0);
        couponDO.setValidStartTime(request.getValidStartTime());
        couponDO.setValidEndTime(request.getValidEndTime());
        couponDO.setStatus(request.getStatus() == null
                ? CommonStatus.ENABLED.getCode() : request.getStatus());
        couponDO.setDescription(trimToNull(request.getDescription()));
        if (couponMapper.insert(couponDO) != 1) {
            throw new BusinessException(500, "新增优惠券失败");
        }
        log.info("新增优惠券成功，优惠券编号: {}, 编码: {}", couponDO.getId(), couponCode);
        return couponDO.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCoupon(Long id, CouponUpdateRequest request) {
        CouponDO existing = getRequiredCoupon(id);
        CouponType couponType = parseCouponType(request.getCouponType());
        validateCouponRules(couponType, request.getDiscountValue(), request.getThresholdAmount(),
                request.getValidStartTime(), request.getValidEndTime());
        validateStatus(request.getStatus());
        if (request.getTotalCount() < existing.getReceivedCount()) {
            throw new BusinessException(400, "发行总量不能小于已领取数量");
        }
        CouponDO couponDO = new CouponDO();
        couponDO.setId(id);
        couponDO.setName(request.getName().trim());
        couponDO.setCouponType(couponType.getCode());
        couponDO.setDiscountValue(request.getDiscountValue());
        couponDO.setThresholdAmount(request.getThresholdAmount() == null
                ? BigDecimal.ZERO : request.getThresholdAmount());
        couponDO.setTotalCount(request.getTotalCount());
        couponDO.setValidStartTime(request.getValidStartTime());
        couponDO.setValidEndTime(request.getValidEndTime());
        couponDO.setStatus(request.getStatus());
        couponDO.setDescription(trimToNull(request.getDescription()));
        if (couponMapper.updateById(couponDO) != 1) {
            throw new BusinessException(500, "修改优惠券失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCoupon(Long id) {
        getRequiredCoupon(id);
        if (couponMapper.deleteById(id) != 1) {
            throw new BusinessException(500, "删除优惠券失败");
        }
        log.info("删除优惠券成功，优惠券编号: {}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long id, CouponStatusRequest request) {
        getRequiredCoupon(id);
        validateStatus(request.getStatus());
        CouponDO couponDO = new CouponDO();
        couponDO.setId(id);
        couponDO.setStatus(request.getStatus());
        if (couponMapper.updateById(couponDO) != 1) {
            throw new BusinessException(500, "修改优惠券状态失败");
        }
    }

    private CouponDO getRequiredCoupon(Long id) {
        CouponDO couponDO = couponMapper.selectById(id);
        if (couponDO == null) {
            throw new BusinessException(404, "优惠券不存在");
        }
        return couponDO;
    }

    private void validateCouponRules(CouponType couponType, BigDecimal discountValue,
                                     BigDecimal thresholdAmount, LocalDateTime validStartTime,
                                     LocalDateTime validEndTime) {
        if (validStartTime.isAfter(validEndTime)) {
            throw new BusinessException(400, "有效期开始时间不能晚于结束时间");
        }
        BigDecimal threshold = thresholdAmount == null ? BigDecimal.ZERO : thresholdAmount;
        if (couponType == CouponType.AMOUNT && threshold.compareTo(BigDecimal.ZERO) > 0
                && discountValue.compareTo(threshold) > 0) {
            throw new BusinessException(400, "满减金额不能大于使用门槛");
        }
        if (couponType == CouponType.DISCOUNT
                && (discountValue.compareTo(BigDecimal.ZERO) <= 0
                || discountValue.compareTo(MAX_DISCOUNT_RATE) > 0)) {
            throw new BusinessException(400, "折扣值必须大于 0 且不超过 9.99");
        }
    }

    private CouponType parseCouponType(String couponType) {
        try {
            return CouponType.fromCode(couponType);
        } catch (IllegalArgumentException exception) {
            throw new BusinessException(400, "优惠券类型只能是 AMOUNT 或 DISCOUNT");
        }
    }

    private void validateCouponType(String couponType) {
        if (StringUtils.hasText(couponType)) {
            parseCouponType(couponType);
        }
    }

    private void validateStatus(Integer status) {
        if (status == null) {
            return;
        }
        try {
            CommonStatus.fromCode(status);
        } catch (IllegalArgumentException exception) {
            throw new BusinessException(400, "优惠券状态只能是 0 或 1");
        }
    }

    private String generateCouponCode() {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        return "CP" + uuid.substring(0, 16).toUpperCase(Locale.ROOT);
    }

    private String trimToNull(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }
}
