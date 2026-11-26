package com.shop.admin.auth.controller;

import com.shop.admin.admin.vo.AdminVO;
import com.shop.admin.auth.dto.ChangePasswordRequest;
import com.shop.admin.auth.dto.LoginRequest;
import com.shop.admin.auth.service.AuthService;
import com.shop.admin.auth.vo.LoginVO;
import com.shop.admin.common.api.ApiResult;
import com.shop.admin.menu.vo.MenuAccessVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理员认证接口。
 *
 * @author shop-admin
 */
@RestController
@RequestMapping("/api/admin/auth")
@RequiredArgsConstructor
@Tag(name = "管理员认证", description = "登录、当前用户、动态菜单权限、修改密码与退出")
public class AuthController {

    private final AuthService authService;

    /**
     * 管理员登录。
     *
     * @param request 登录请求
     * @return 登录响应
     */
    @PostMapping("/login")
    @Operation(summary = "管理员登录", description = "验证用户名和密码，返回 JWT 访问令牌")
    public ApiResult<LoginVO> login(@Valid @RequestBody LoginRequest request) {
        return ApiResult.success(authService.login(request));
    }

    /**
     * 获取当前登录管理员信息。
     *
     * @return 当前管理员信息
     */
    @GetMapping("/me")
    @Operation(summary = "获取当前管理员信息")
    @SecurityRequirement(name = "bearerAuth")
    public ApiResult<AdminVO> me() {
        return ApiResult.success(authService.getCurrentAdmin());
    }

    /**
     * 获取当前管理员可显示的菜单树与接口权限。
     *
     * @return 菜单权限信息
     */
    @GetMapping("/menus")
    @Operation(summary = "获取当前管理员菜单权限",
            description = "根据角色菜单分配实时返回可显示菜单树与接口权限编码，前端据此渲染菜单")
    @SecurityRequirement(name = "bearerAuth")
    public ApiResult<MenuAccessVO> menus() {
        return ApiResult.success(authService.getCurrentMenuAccess());
    }

    /**
     * 修改当前管理员密码。
     *
     * @param request 修改密码请求
     * @return 操作结果
     */
    @PutMapping("/password")
    @Operation(summary = "修改当前管理员密码")
    @SecurityRequirement(name = "bearerAuth")
    public ApiResult<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        authService.changePassword(request);
        return ApiResult.success();
    }

    /**
     * 退出登录。JWT 为无状态令牌，客户端需同时删除本地保存的令牌。
     *
     * @return 操作结果
     */
    @PostMapping("/logout")
    @Operation(summary = "退出登录", description = "清除当前请求的安全上下文，客户端应删除本地令牌")
    @SecurityRequirement(name = "bearerAuth")
    public ApiResult<Void> logout() {
        SecurityContextHolder.clearContext();
        return ApiResult.success();
    }
}
