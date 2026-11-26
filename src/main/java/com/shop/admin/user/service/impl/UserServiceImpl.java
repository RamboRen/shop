package com.shop.admin.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shop.admin.common.api.PageResult;
import com.shop.admin.common.constant.CommonConstants;
import com.shop.admin.common.enums.CommonStatus;
import com.shop.admin.common.enums.Gender;
import com.shop.admin.common.enums.OrderStatus;
import com.shop.admin.common.exception.BusinessException;
import com.shop.admin.order.entity.OrderDO;
import com.shop.admin.order.mapper.OrderMapper;
import com.shop.admin.security.SecurityUtils;
import com.shop.admin.user.dto.UserCreateRequest;
import com.shop.admin.user.dto.UserQueryRequest;
import com.shop.admin.user.dto.UserStatusRequest;
import com.shop.admin.user.dto.UserUpdateRequest;
import com.shop.admin.user.entity.UserDO;
import com.shop.admin.user.mapper.UserMapper;
import com.shop.admin.user.service.UserService;
import com.shop.admin.user.vo.UserDetailVO;
import com.shop.admin.user.vo.UserVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

/**
 * 商城用户服务实现。
 *
 * @author shop-admin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    private final OrderMapper orderMapper;

    @Override
    public PageResult<UserVO> pageUsers(UserQueryRequest request) {
        validateDateRange(request);
        validateGender(request.getGender());
        validateStatus(request.getStatus());
        int pageNum = request.getPageNum() == null ? CommonConstants.DEFAULT_PAGE_NUM : request.getPageNum();
        int pageSize = request.getPageSize() == null ? CommonConstants.DEFAULT_PAGE_SIZE : request.getPageSize();

        LambdaQueryWrapper<UserDO> wrapper = Wrappers.<UserDO>lambdaQuery();
        String keyword = trimToNull(request.getKeyword());
        if (keyword != null) {
            wrapper.and(query -> query.like(UserDO::getUserNo, keyword)
                    .or()
                    .like(UserDO::getUsername, keyword)
                    .or()
                    .like(UserDO::getNickname, keyword)
                    .or()
                    .like(UserDO::getPhone, keyword));
        }
        if (StringUtils.hasText(request.getGender())) {
            wrapper.eq(UserDO::getGender, request.getGender());
        }
        if (request.getStatus() != null) {
            wrapper.eq(UserDO::getStatus, request.getStatus());
        }
        if (request.getStartDate() != null) {
            wrapper.ge(UserDO::getCreateTime, request.getStartDate().atStartOfDay());
        }
        if (request.getEndDate() != null) {
            wrapper.lt(UserDO::getCreateTime, request.getEndDate().plusDays(1).atStartOfDay());
        }
        wrapper.orderByDesc(UserDO::getCreateTime).orderByDesc(UserDO::getId);

        Page<UserDO> userPage = userMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        List<UserVO> records = userPage.getRecords().stream()
                .map(UserVO::from)
                .toList();
        return PageResult.of(userPage.getTotal(), userPage.getCurrent(), userPage.getSize(), records);
    }

    @Override
    public UserDetailVO getUser(Long id) {
        UserDO userDO = getRequiredUser(id);
        Long orderCount = orderMapper.selectCount(Wrappers.<OrderDO>lambdaQuery()
                .eq(OrderDO::getUserId, id));
        BigDecimal totalSpent = sumPaidAmount(id);
        return UserDetailVO.from(userDO, orderCount, totalSpent);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createUser(UserCreateRequest request) {
        String username = request.getUsername().trim();
        String phone = trimToNull(request.getPhone());
        validateStatus(request.getStatus());
        validateUnique(username, phone, null);

        String userNo = generateUserNo();
        UserDO userDO = new UserDO();
        userDO.setUserNo(userNo);
        userDO.setUsername(username);
        userDO.setNickname(request.getNickname().trim());
        userDO.setPhone(phone);
        userDO.setEmail(trimToNull(request.getEmail()));
        userDO.setAvatar(trimToNull(request.getAvatar()));
        userDO.setGender(parseGender(request.getGender()));
        userDO.setBirthday(request.getBirthday());
        userDO.setStatus(request.getStatus() == null
                ? CommonStatus.ENABLED.getCode() : request.getStatus());
        userDO.setRemark(trimToNull(request.getRemark()));
        if (userMapper.insert(userDO) != 1) {
            throw new BusinessException(500, "新增用户失败");
        }
        log.info("新增用户成功，用户编号: {}, 用户编号编码: {}, 操作人: {}",
                userDO.getId(), userNo, SecurityUtils.getCurrentAdminId());
        return userDO.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUser(Long id, UserUpdateRequest request) {
        UserDO existing = getRequiredUser(id);
        String phone = trimToNull(request.getPhone());
        validateUnique(existing.getUsername(), phone, id);

        UserDO updateDO = new UserDO();
        updateDO.setId(id);
        updateDO.setNickname(request.getNickname().trim());
        updateDO.setPhone(phone);
        updateDO.setEmail(trimToNull(request.getEmail()));
        updateDO.setAvatar(trimToNull(request.getAvatar()));
        updateDO.setGender(parseGender(request.getGender()));
        updateDO.setBirthday(request.getBirthday());
        updateDO.setRemark(trimToNull(request.getRemark()));
        if (userMapper.updateById(updateDO) != 1) {
            throw new BusinessException(500, "修改用户失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(Long id) {
        getRequiredUser(id);
        if (userMapper.deleteById(id) != 1) {
            throw new BusinessException(500, "删除用户失败");
        }
        log.info("删除用户成功，用户编号: {}, 操作人: {}", id, SecurityUtils.getCurrentAdminId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long id, UserStatusRequest request) {
        UserDO existing = getRequiredUser(id);
        validateStatus(request.getStatus());
        if (existing.getStatus().equals(request.getStatus())) {
            return;
        }
        // 以读取时的状态作为更新条件，避免并发操作互相覆盖
        UserDO updateDO = new UserDO();
        updateDO.setId(id);
        updateDO.setStatus(request.getStatus());
        int updated = userMapper.update(updateDO, Wrappers.<UserDO>lambdaUpdate()
                .eq(UserDO::getId, id)
                .eq(UserDO::getStatus, existing.getStatus()));
        if (updated != 1) {
            throw new BusinessException(409, "用户状态已被其他操作修改，请刷新后重试");
        }
    }

    private BigDecimal sumPaidAmount(Long userId) {
        QueryWrapper<OrderDO> wrapper = Wrappers.<OrderDO>query();
        wrapper.select("COALESCE(SUM(pay_amount), 0) AS total_spent")
                .eq("user_id", userId)
                .in("status", OrderStatus.PAID.getCode(), OrderStatus.SHIPPED.getCode(),
                        OrderStatus.COMPLETED.getCode());
        List<Map<String, Object>> rows = orderMapper.selectMaps(wrapper);
        if (rows.isEmpty() || rows.get(0).get("total_spent") == null) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(rows.get(0).get("total_spent").toString());
    }

    private void validateUnique(String username, String phone, Long excludeId) {
        if (userMapper.selectCount(Wrappers.<UserDO>lambdaQuery()
                .eq(UserDO::getUsername, username)
                .ne(excludeId != null, UserDO::getId, excludeId)) > 0) {
            throw new BusinessException(409, "登录账号已存在");
        }
        if (StringUtils.hasText(phone) && userMapper.selectCount(Wrappers.<UserDO>lambdaQuery()
                .eq(UserDO::getPhone, phone)
                .ne(excludeId != null, UserDO::getId, excludeId)) > 0) {
            throw new BusinessException(409, "手机号已存在");
        }
    }

    private void validateDateRange(UserQueryRequest request) {
        if (request.getStartDate() != null && request.getEndDate() != null
                && request.getStartDate().isAfter(request.getEndDate())) {
            throw new BusinessException(400, "开始日期不能晚于结束日期");
        }
    }

    private void validateGender(String gender) {
        if (StringUtils.hasText(gender)) {
            try {
                Gender.valueOf(gender);
            } catch (IllegalArgumentException exception) {
                throw new BusinessException(400, "性别只能是 MALE、FEMALE 或 UNKNOWN");
            }
        }
    }

    private void validateStatus(Integer status) {
        if (status == null) {
            return;
        }
        try {
            CommonStatus.fromCode(status);
        } catch (IllegalArgumentException exception) {
            throw new BusinessException(400, "用户状态只能是 0 或 1");
        }
    }

    private String parseGender(String gender) {
        return StringUtils.hasText(gender) ? Gender.valueOf(gender).getCode()
                : Gender.UNKNOWN.getCode();
    }

    private String generateUserNo() {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        return "U" + uuid.substring(0, 12).toUpperCase(Locale.ROOT);
    }

    private String trimToNull(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }

    private UserDO getRequiredUser(Long id) {
        UserDO userDO = userMapper.selectById(id);
        if (userDO == null) {
            throw new BusinessException(404, "用户不存在");
        }
        return userDO;
    }
}
