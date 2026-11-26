package com.shop.admin.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * JWT 组件配置。
 *
 * @author shop-admin
 */
@Configuration
public class JwtConfig {

    /**
     * 创建 JWT 令牌组件。
     *
     * @param jwtProperties JWT 配置
     * @return 令牌组件
     */
    @Bean
    public JwtTokenProvider jwtTokenProvider(JwtProperties jwtProperties) {
        return new JwtTokenProvider(jwtProperties);
    }
}
