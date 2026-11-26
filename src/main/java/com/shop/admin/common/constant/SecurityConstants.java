package com.shop.admin.common.constant;

/**
 * 安全认证相关常量。
 *
 * @author shop-admin
 */
public final class SecurityConstants {

    /**
     * 认证请求头名称。
     */
    public static final String AUTH_HEADER = "Authorization";

    /**
     * 令牌前缀。
     */
    public static final String TOKEN_PREFIX = "Bearer ";

    /**
     * JWT 中管理员编号的声明名称。
     */
    public static final String CLAIM_ADMIN_ID = "adminId";

    /**
     * JWT 中角色编码的声明名称。
     */
    public static final String CLAIM_ROLE = "role";

    /**
     * 角色权限前缀。
     */
    public static final String ROLE_PREFIX = "ROLE_";

    /**
     * 登录接口地址。
     */
    public static final String LOGIN_PATH = "/api/admin/auth/login";

    /**
     * 上传文件访问路径。
     */
    public static final String UPLOAD_PATH_PATTERN = "/uploads/**";

    /**
     * 令牌类型名称。
     */
    public static final String TOKEN_TYPE = "Bearer";

    private SecurityConstants() {
    }
}
