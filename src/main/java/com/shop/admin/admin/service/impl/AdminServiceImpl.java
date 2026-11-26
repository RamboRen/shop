package com.shop.admin.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shop.admin.admin.dto.AdminCreateRequest;
import com.shop.admin.admin.dto.AdminPasswordResetRequest;
import com.shop.admin.admin.dto.AdminQueryRequest;
import com.shop.admin.admin.dto.AdminStatusRequest;
import com.shop.admin.admin.dto.AdminUpdateRequest;
import com.shop.admin.admin.entity.AdminDO;
import com.shop.admin.admin.mapper.AdminMapper;
import com.shop.admin.admin.service.AdminService;
import com.shop.admin.admin.vo.AdminVO;
import com.shop.admin.common.api.PageResult;
import com.shop.admin.common.constant.CommonConstants;
import com.shop.admin.common.enums.AdminRole;
import com.shop.admin.common.enums.CommonStatus;
import com.shop.admin.common.exception.BusinessException;
import com.shop.admin.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * 管理员管理服务实现。
 *
 * @author shop-admin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final AdminMapper adminMapper;

    private final PasswordEncoder passwordEncoder;

    @Override
    public PageResult<AdminVO> pageAdmins(AdminQueryRequest request) {
        int pageNum = request.getPageNum() == null ? CommonConstants.DEFAULT_PAGE_NUM : request.getPageNum();
        int pageSize = request.getPageSize() == null ? CommonConstants.DEFAULT_PAGE_SIZE : request.getPageSize();
        LambdaQueryWrapper<AdminDO> wrapper = Wrappers.<AdminDO>lambdaQuery();
        String keyword = trimToNull(request.getKeyword());
        if (keyword != null) {
            wrapper.and(query -> query.like(AdminDO::getUsername, keyword)
                    .or()
                    .like(AdminDO::getNickname, keyword));
        }
        if (StringUtils.hasText(request.getRoleCode())) {
            wrapper.eq(AdminDO::getRoleCode, request.getRoleCode());
        }
        if (request.getStatus() != null) {
            wrapper.eq(AdminDO::getStatus, request.getStatus());
        }
        wrapper.orderByDesc(AdminDO::getCreateTime);

        Page<AdminDO> adminPage = adminMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        List<AdminVO> records = adminPage.getRecords().stream()
                .map(AdminVO::from)
                .toList();
        return PageResult.of(adminPage.getTotal(), adminPage.getCurrent(), adminPage.getSize(), records);
    }

    @Override
    public AdminVO getAdmin(Long id) {
        return AdminVO.from(getRequiredAdmin(id));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createAdmin(AdminCreateRequest request) {
        String username = request.getUsername().trim();
        if (adminMapper.selectCount(Wrappers.<AdminDO>lambdaQuery()
                .eq(AdminDO::getUsername, username)) > 0) {
            throw new BusinessException(409, "用户名已存在");
        }
        AdminRole role = parseRole(request.getRoleCode());
        AdminDO adminDO = new AdminDO();
        adminDO.setUsername(username);
        adminDO.setPassword(passwordEncoder.encode(request.getPassword()));
        adminDO.setNickname(request.getNickname().trim());
        adminDO.setAvatar(trimToNull(request.getAvatar()));
        adminDO.setPhone(trimToNull(request.getPhone()));
        adminDO.setEmail(trimToNull(request.getEmail()));
        adminDO.setRoleCode(role.getCode());
        adminDO.setStatus(request.getStatus() == null
                ? CommonStatus.ENABLED.getCode() : request.getStatus());
        adminDO.setRemark(trimToNull(request.getRemark()));
        if (adminMapper.insert(adminDO) != 1) {
            throw new BusinessException(500, "新增管理员失败");
        }
        log.info("新增管理员成功，管理员编号: {}, 操作人: {}",
                adminDO.getId(), SecurityUtils.getCurrentAdminId());
        return adminDO.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAdmin(Long id, AdminUpdateRequest request) {
        AdminDO adminDO = getRequiredAdmin(id);
        AdminRole targetRole = parseRole(request.getRoleCode());
        boolean roleChanged = !Objects.equals(adminDO.getRoleCode(), targetRole.getCode());
        if (roleChanged && Objects.equals(id, SecurityUtils.getCurrentAdminId())) {
            throw new BusinessException(400, "不能修改当前登录管理员的角色");
        }
        if (roleChanged && AdminRole.SUPER_ADMIN.getCode().equals(adminDO.getRoleCode())) {
            ensureAnotherEnabledSuperAdmin(id);
        }
        LambdaUpdateWrapper<AdminDO> wrapper = Wrappers.<AdminDO>lambdaUpdate()
                .eq(AdminDO::getId, id)
                .set(AdminDO::getNickname, request.getNickname().trim())
                .set(AdminDO::getAvatar, trimToNull(request.getAvatar()))
                .set(AdminDO::getPhone, trimToNull(request.getPhone()))
                .set(AdminDO::getEmail, trimToNull(request.getEmail()))
                .set(AdminDO::getRoleCode, targetRole.getCode())
                .set(AdminDO::getRemark, trimToNull(request.getRemark()))
                .set(AdminDO::getUpdateTime, LocalDateTime.now());
        if (adminMapper.update(null, wrapper) != 1) {
            throw new BusinessException(500, "修改管理员失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAdmin(Long id) {
        Long currentAdminId = SecurityUtils.getCurrentAdminId();
        if (Objects.equals(id, currentAdminId)) {
            throw new BusinessException(400, "不能删除当前登录管理员");
        }
        AdminDO adminDO = getRequiredAdmin(id);
        if (AdminRole.SUPER_ADMIN.getCode().equals(adminDO.getRoleCode())) {
            ensureAnotherEnabledSuperAdmin(id);
        }
        if (adminMapper.deleteById(id) != 1) {
            throw new BusinessException(500, "删除管理员失败");
        }
        log.info("删除管理员成功，管理员编号: {}, 操作人: {}", id, currentAdminId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long id, AdminStatusRequest request) {
        Long currentAdminId = SecurityUtils.getCurrentAdminId();
        AdminDO adminDO = getRequiredAdmin(id);
        if (Objects.equals(id, currentAdminId)
                && CommonStatus.DISABLED.getCode() == request.getStatus()) {
            throw new BusinessException(400, "不能禁用当前登录管理员");
        }
        if (CommonStatus.DISABLED.getCode() == request.getStatus()
                && AdminRole.SUPER_ADMIN.getCode().equals(adminDO.getRoleCode())) {
            ensureAnotherEnabledSuperAdmin(id);
        }
        AdminDO updateDO = new AdminDO();
        updateDO.setId(id);
        updateDO.setStatus(request.getStatus());
        if (adminMapper.updateById(updateDO) != 1) {
            throw new BusinessException(500, "修改管理员状态失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resetPassword(Long id, AdminPasswordResetRequest request) {
        getRequiredAdmin(id);
        AdminDO updateDO = new AdminDO();
        updateDO.setId(id);
        updateDO.setPassword(passwordEncoder.encode(request.getPassword()));
        if (adminMapper.updateById(updateDO) != 1) {
            throw new BusinessException(500, "重置密码失败");
        }
        log.info("重置管理员密码，管理员编号: {}, 操作人: {}", id, SecurityUtils.getCurrentAdminId());
    }

    private AdminDO getRequiredAdmin(Long id) {
        AdminDO adminDO = adminMapper.selectById(id);
        if (adminDO == null) {
            throw new BusinessException(404, "管理员不存在");
        }
        return adminDO;
    }

    private AdminRole parseRole(String roleCode) {
        if (!AdminRole.isValidCode(roleCode)) {
            throw new BusinessException(400, "未知的管理员角色");
        }
        return AdminRole.fromCode(roleCode);
    }

    private void ensureAnotherEnabledSuperAdmin(Long excludeAdminId) {
        long enabledSuperAdminCount = adminMapper.selectCount(Wrappers.<AdminDO>lambdaQuery()
                .eq(AdminDO::getRoleCode, AdminRole.SUPER_ADMIN.getCode())
                .eq(AdminDO::getStatus, CommonStatus.ENABLED.getCode())
                .ne(AdminDO::getId, excludeAdminId));
        if (enabledSuperAdminCount == 0) {
            throw new BusinessException(400, "必须至少保留一名启用的超级管理员");
        }
    }

    private String trimToNull(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }
}
