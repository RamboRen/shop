package com.shop.admin.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * 管理员登录请求。
 *
 * @author shop-admin
 */
@Getter
@Setter
@Schema(description = "管理员登录请求")
public class LoginRequest {

    @NotBlank(message = "用户名不能为空")
    @Size(max = 20, message = "用户名长度不能超过 20 位")
    @Schema(description = "登录用户名", example = "admin")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(max = 32, message = "密码长度不能超过 32 位")
    @Schema(description = "登录密码", example = "admin123")
    private String password;
}
