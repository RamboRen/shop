package com.shop.admin.auth.service.impl;

import com.shop.admin.admin.entity.AdminDO;
import com.shop.admin.admin.mapper.AdminMapper;
import com.shop.admin.admin.vo.AdminVO;
import com.shop.admin.auth.dto.ChangePasswordRequest;
import com.shop.admin.auth.dto.LoginRequest;
import com.shop.admin.auth.service.AuthService;
import com.shop.admin.auth.vo.LoginVO;
import com.shop.admin.common.constant.SecurityConstants;
import com.shop.admin.common.exception.BusinessException;
import com.shop.admin.menu.service.MenuService;
import com.shop.admin.menu.vo.MenuAccessVO;
import com.shop.admin.security.AdminPrincipal;
import com.shop.admin.security.JwtTokenProvider;
import com.shop.admin.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 认证服务实现。
 *
 * @author shop-admin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;

    private final AdminMapper adminMapper;

    private final PasswordEncoder passwordEncoder;

    private final JwtTokenProvider jwtTokenProvider;

    private final MenuService menuService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LoginVO login(LoginRequest request) {
        String username = request.getUsername().trim();
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, request.getPassword()));
        } catch (AuthenticationException exception) {
            log.warn("管理员登录失败，用户名: {}", username);
            throw new BusinessException(401, "用户名或密码错误");
        }
        AdminPrincipal principal = (AdminPrincipal) authentication.getPrincipal();
        AdminDO adminDO = adminMapper.selectById(principal.getAdminId());
        if (adminDO == null) {
            throw new BusinessException(401, "管理员不存在或已被删除");
        }
        AdminDO updateDO = new AdminDO();
        updateDO.setId(adminDO.getId());
        updateDO.setLastLoginTime(LocalDateTime.now());
        adminMapper.updateById(updateDO);

        String token = jwtTokenProvider.generateToken(principal);
        MenuAccessVO menuAccess = menuService.getMenuAccessByRole(principal.getRoleCode());
        return new LoginVO(token, SecurityConstants.TOKEN_TYPE,
                jwtTokenProvider.getExpirationSeconds(), AdminVO.from(adminDO),
                menuAccess.getMenus(), menuAccess.getPermissions());
    }

    @Override
    public MenuAccessVO getCurrentMenuAccess() {
        return menuService.getCurrentMenuAccess();
    }

    @Override
    public AdminVO getCurrentAdmin() {
        Long adminId = SecurityUtils.getCurrentAdminId();
        AdminDO adminDO = adminMapper.selectById(adminId);
        if (adminDO == null) {
            throw new BusinessException(401, "管理员不存在或已被删除");
        }
        return AdminVO.from(adminDO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changePassword(ChangePasswordRequest request) {
        Long adminId = SecurityUtils.getCurrentAdminId();
        AdminDO adminDO = adminMapper.selectById(adminId);
        if (adminDO == null) {
            throw new BusinessException(401, "管理员不存在或已被删除");
        }
        if (!passwordEncoder.matches(request.getOldPassword(), adminDO.getPassword())) {
            throw new BusinessException(400, "原密码不正确");
        }
        if (passwordEncoder.matches(request.getNewPassword(), adminDO.getPassword())) {
            throw new BusinessException(400, "新密码不能与原密码相同");
        }
        AdminDO updateDO = new AdminDO();
        updateDO.setId(adminId);
        updateDO.setPassword(passwordEncoder.encode(request.getNewPassword()));
        adminMapper.updateById(updateDO);
    }
}
