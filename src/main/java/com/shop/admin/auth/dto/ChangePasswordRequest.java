package com.shop.admin.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * 修改当前管理员密码请求。
 *
 * @author shop-admin
 */
@Getter
@Setter
@Schema(description = "修改当前管理员密码请求")
public class ChangePasswordRequest {

    @NotBlank(message = "原密码不能为空")
    @Size(max = 32, message = "原密码长度不能超过 32 位")
    @Schema(description = "原密码", example = "admin123")
    private String oldPassword;

    @NotBlank(message = "新密码不能为空")
    @Size(min = 6, max = 32, message = "新密码长度必须为 6 到 32 位")
    @Schema(description = "新密码", example = "newAdmin123")
    private String newPassword;
}
