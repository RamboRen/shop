package com.shop.admin.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger / OpenAPI 文档配置。
 *
 * @author shop-admin
 */
@Configuration
public class OpenApiConfig {

    private static final String SECURITY_SCHEME_NAME = "bearerAuth";

    /**
     * 构建接口文档元信息与 JWT 认证方案。
     *
     * @return OpenAPI 配置
     */
    @Bean
    public OpenAPI shopAdminOpenApi() {
        Info info = new Info()
                .title("商城后台管理系统 API")
                .version("1.4.0")
                .description("管理员登录、动态菜单权限、订单、商品规格、优惠券、数据统计、图库与商品管理接口")
                .contact(new Contact().name("shop-admin"));
        SecurityScheme securityScheme = new SecurityScheme()
                .name(SECURITY_SCHEME_NAME)
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("登录后获取的 JWT 访问令牌，格式为 Bearer {token}");
        return new OpenAPI()
                .info(info)
                .components(new Components().addSecuritySchemes(SECURITY_SCHEME_NAME, securityScheme));
    }
}
