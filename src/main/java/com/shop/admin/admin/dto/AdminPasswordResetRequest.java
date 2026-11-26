package com.shop.admin.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * 重置管理员密码请求。
 *
 * @author shop-admin
 */
@Getter
@Setter
@Schema(description = "重置管理员密码请求")
public class AdminPasswordResetRequest {

    @NotBlank(message = "新密码不能为空")
    @Size(min = 6, max = 32, message = "密码长度必须为 6 到 32 位")
    @Schema(description = "新密码", example = "newPass123")
    private String password;
}
