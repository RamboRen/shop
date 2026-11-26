package com.shop.admin.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * 新增管理员请求。
 *
 * @author shop-admin
 */
@Getter
@Setter
@Schema(description = "新增管理员请求")
public class AdminCreateRequest {

    @NotBlank(message = "用户名不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9_]{4,20}$", message = "用户名必须为 4 到 20 位字母、数字或下划线")
    @Schema(description = "登录用户名", example = "shop_admin")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 32, message = "密码长度必须为 6 到 32 位")
    @Schema(description = "登录密码", example = "admin123")
    private String password;

    @NotBlank(message = "昵称不能为空")
    @Size(max = 30, message = "昵称长度不能超过 30 位")
    @Schema(description = "昵称", example = "商品管理员")
    private String nickname;

    @Size(max = 255, message = "头像地址长度不能超过 255 位")
    @Schema(description = "头像地址")
    private String avatar;

    @Pattern(regexp = "^$|^1[3-9]\\d{9}$", message = "手机号格式不正确")
    @Schema(description = "手机号", example = "13800138000")
    private String phone;

    @Email(message = "邮箱格式不正确")
    @Size(max = 100, message = "邮箱长度不能超过 100 位")
    @Schema(description = "邮箱", example = "admin@example.com")
    private String email;

    @NotBlank(message = "角色不能为空")
    @Pattern(regexp = "SUPER_ADMIN|ADMIN", message = "角色只能是 SUPER_ADMIN 或 ADMIN")
    @Schema(description = "角色编码", example = "ADMIN")
    private String roleCode;

    @Min(value = 0, message = "状态只能是 0 或 1")
    @Max(value = 1, message = "状态只能是 0 或 1")
    @Schema(description = "状态，0 禁用，1 启用", example = "1")
    private Integer status;

    @Size(max = 200, message = "备注长度不能超过 200 位")
    @Schema(description = "备注")
    private String remark;
}
