package com.shop.admin.auth.service;

import com.shop.admin.admin.vo.AdminVO;
import com.shop.admin.auth.dto.ChangePasswordRequest;
import com.shop.admin.auth.dto.LoginRequest;
import com.shop.admin.auth.vo.LoginVO;
import com.shop.admin.menu.vo.MenuAccessVO;

/**
 * 认证服务接口。
 *
 * @author shop-admin
 */
public interface AuthService {

    /**
     * 管理员登录。
     *
     * @param request 登录请求
     * @return 登录响应
     */
    LoginVO login(LoginRequest request);

    /**
     * 获取当前登录管理员信息。
     *
     * @return 管理员信息
     */
    AdminVO getCurrentAdmin();

    /**
     * 获取当前登录管理员的菜单树与接口权限。
     *
     * @return 菜单权限信息
     */
    MenuAccessVO getCurrentMenuAccess();

    /**
     * 修改当前管理员密码。
     *
     * @param request 修改密码请求
     */
    void changePassword(ChangePasswordRequest request);
}
