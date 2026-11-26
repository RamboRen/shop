package com.shop.admin.security;

import com.shop.admin.common.exception.BusinessException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 当前登录管理员工具类。
 *
 * @author shop-admin
 */
public final class SecurityUtils {

    private SecurityUtils() {
    }

    /**
     * 获取当前登录管理员认证主体。
     *
     * @return 管理员认证主体
     */
    public static AdminPrincipal getCurrentPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken
                || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof AdminPrincipal principal)) {
            throw new BusinessException(401, "未登录或登录已过期");
        }
        return principal;
    }

    /**
     * 获取当前登录管理员编号。
     *
     * @return 管理员编号
     */
    public static Long getCurrentAdminId() {
        return getCurrentPrincipal().getAdminId();
    }
}
