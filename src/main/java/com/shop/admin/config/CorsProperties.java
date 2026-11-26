package com.shop.admin.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * 跨域配置属性。
 *
 * @author shop-admin
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "app.cors")
public class CorsProperties {

    /**
     * 允许的前端来源。
     */
    private List<String> allowedOrigins = List.of(
            "http://localhost:5173",
            "http://127.0.0.1:5173",
            "http://localhost:3000",
            "http://127.0.0.1:3000");

    /**
     * 允许的请求方法。
     */
    private List<String> allowedMethods = List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS");

    /**
     * 允许的请求头。
     */
    private List<String> allowedHeaders = List.of("*");

    /**
     * 暴露给前端的响应头。
     */
    private List<String> exposedHeaders = List.of("Authorization", "Content-Disposition");

    /**
     * 是否允许携带认证信息。
     */
    private boolean allowCredentials = true;

    /**
     * 预检请求缓存秒数。
     */
    private long maxAge = 3600L;
}
