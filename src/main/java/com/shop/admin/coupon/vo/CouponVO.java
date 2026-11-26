package com.shop.admin.coupon.vo;

import com.shop.admin.common.enums.CommonStatus;
import com.shop.admin.common.enums.CouponType;
import com.shop.admin.coupon.entity.CouponDO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 优惠券视图对象。
 *
 * @author shop-admin
 */
@Getter
@Setter
@Schema(description = "优惠券信息")
public class CouponVO {

    @Schema(description = "优惠券编号", example = "1")
    private Long id;

    @Schema(description = "优惠券编码", example = "CP20260001")
    private String couponCode;

    @Schema(description = "优惠券名称", example = "新人满减券")
    private String name;

    @Schema(description = "类型，AMOUNT 满减券，DISCOUNT 折扣券", example = "AMOUNT")
    private String couponType;

    @Schema(description = "类型名称", example = "满减券")
    private String couponTypeName;

    @Schema(description = "满减金额或折扣值", example = "20.00")
    private BigDecimal discountValue;

    @Schema(description = "使用门槛，0 表示无门槛", example = "100.00")
    private BigDecimal thresholdAmount;

    @Schema(description = "发行总量", example = "1000")
    private Integer totalCount;

    @Schema(description = "已领取数量", example = "260")
    private Integer receivedCount;

    @Schema(description = "已使用数量", example = "120")
    private Integer usedCount;

    @Schema(description = "剩余数量", example = "740")
    private Integer remainingCount;

    @Schema(description = "有效期开始时间")
    private LocalDateTime validStartTime;

    @Schema(description = "有效期结束时间")
    private LocalDateTime validEndTime;

    @Schema(description = "是否已过期", example = "false")
    private Boolean expired;

    @Schema(description = "状态，0 禁用，1 启用", example = "1")
    private Integer status;

    @Schema(description = "状态名称", example = "启用")
    private String statusName;

    @Schema(description = "使用说明")
    private String description;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    /**
     * 将数据对象转换为视图对象。
     *
     * @param couponDO 优惠券数据对象
     * @return 优惠券视图对象
     */
    public static CouponVO from(CouponDO couponDO) {
        CouponVO couponVO = new CouponVO();
        couponVO.setId(couponDO.getId());
        couponVO.setCouponCode(couponDO.getCouponCode());
        couponVO.setName(couponDO.getName());
        couponVO.setCouponType(couponDO.getCouponType());
        couponVO.setCouponTypeName(CouponType.fromCode(couponDO.getCouponType()).getDescription());
        couponVO.setDiscountValue(couponDO.getDiscountValue());
        couponVO.setThresholdAmount(couponDO.getThresholdAmount());
        couponVO.setTotalCount(couponDO.getTotalCount());
        couponVO.setReceivedCount(couponDO.getReceivedCount());
        couponVO.setUsedCount(couponDO.getUsedCount());
        couponVO.setRemainingCount(Math.max(couponDO.getTotalCount() - couponDO.getReceivedCount(), 0));
        couponVO.setValidStartTime(couponDO.getValidStartTime());
        couponVO.setValidEndTime(couponDO.getValidEndTime());
        couponVO.setExpired(LocalDateTime.now().isAfter(couponDO.getValidEndTime()));
        couponVO.setStatus(couponDO.getStatus());
        couponVO.setStatusName(CommonStatus.fromCode(couponDO.getStatus()).getDescription());
        couponVO.setDescription(couponDO.getDescription());
        couponVO.setCreateTime(couponDO.getCreateTime());
        couponVO.setUpdateTime(couponDO.getUpdateTime());
        return couponVO;
    }
}
