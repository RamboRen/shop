package com.shop.admin.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * JWT 配置属性。
 *
 * @author shop-admin
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "security.jwt")
public class JwtProperties {

    /**
     * HMAC 签名密钥，长度不少于 32 个字符。
     */
    private String secret;

    /**
     * 令牌签发方。
     */
    private String issuer = "shop-admin";

    /**
     * 令牌有效期，单位为分钟。
     */
    private long expirationMinutes = 120L;
}
