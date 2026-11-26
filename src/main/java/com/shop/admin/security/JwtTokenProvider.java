package com.shop.admin.security;

import com.shop.admin.common.constant.SecurityConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT 令牌生成与解析组件。
 *
 * @author shop-admin
 */
public class JwtTokenProvider {

    private static final int MIN_SECRET_LENGTH = 32;

    private final JwtProperties jwtProperties;

    private final SecretKey secretKey;

    /**
     * 构造令牌组件并校验密钥长度。
     *
     * @param jwtProperties JWT 配置
     */
    public JwtTokenProvider(JwtProperties jwtProperties) {
        if (!StringUtils.hasText(jwtProperties.getSecret())
                || jwtProperties.getSecret().length() < MIN_SECRET_LENGTH) {
            throw new IllegalStateException("security.jwt.secret 长度必须不少于 32 个字符");
        }
        byte[] secretBytes = jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8);
        this.jwtProperties = jwtProperties;
        this.secretKey = Keys.hmacShaKeyFor(secretBytes);
    }

    /**
     * 为认证主体生成访问令牌。
     *
     * @param principal 管理员认证主体
     * @return JWT 令牌
     */
    public String generateToken(AdminPrincipal principal) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + getExpirationSeconds() * 1000L);
        return Jwts.builder()
                .subject(principal.getUsername())
                .claim(SecurityConstants.CLAIM_ADMIN_ID, principal.getAdminId())
                .claim(SecurityConstants.CLAIM_ROLE, principal.getRoleCode())
                .issuer(jwtProperties.getIssuer())
                .issuedAt(now)
                .expiration(expiration)
                .signWith(secretKey)
                .compact();
    }

    /**
     * 解析并校验令牌签名、签发方与有效期。
     *
     * @param token JWT 令牌
     * @return 令牌声明
     */
    public Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .requireIssuer(jwtProperties.getIssuer())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 从令牌中获取用户名。
     *
     * @param token JWT 令牌
     * @return 用户名
     */
    public String getUsername(String token) {
        return parseClaims(token).getSubject();
    }

    /**
     * 判断令牌是否与指定用户匹配且用户处于启用状态。
     *
     * @param token       JWT 令牌
     * @param userDetails 用户详情
     * @return 是否有效
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        Claims claims = parseClaims(token);
        return claims.getSubject().equals(userDetails.getUsername())
                && userDetails.isEnabled()
                && claims.getExpiration().after(new Date());
    }

    /**
     * 获取令牌有效期秒数。
     *
     * @return 有效期秒数
     */
    public long getExpirationSeconds() {
        return jwtProperties.getExpirationMinutes() * 60L;
    }
}
