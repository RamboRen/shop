package com.shop.admin.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * 修改管理员请求。
 *
 * @author shop-admin
 */
@Getter
@Setter
@Schema(description = "修改管理员请求")
public class AdminUpdateRequest {

    @NotBlank(message = "昵称不能为空")
    @Size(max = 30, message = "昵称长度不能超过 30 位")
    @Schema(description = "昵称", example = "运营管理员")
    private String nickname;

    @Size(max = 255, message = "头像地址长度不能超过 255 位")
    @Schema(description = "头像地址")
    private String avatar;

    @Pattern(regexp = "^$|^1[3-9]\\d{9}$", message = "手机号格式不正确")
    @Schema(description = "手机号")
    private String phone;

    @Email(message = "邮箱格式不正确")
    @Size(max = 100, message = "邮箱长度不能超过 100 位")
    @Schema(description = "邮箱")
    private String email;

    @NotBlank(message = "角色不能为空")
    @Pattern(regexp = "SUPER_ADMIN|ADMIN", message = "角色只能是 SUPER_ADMIN 或 ADMIN")
    @Schema(description = "角色编码", example = "ADMIN")
    private String roleCode;

    @Size(max = 200, message = "备注长度不能超过 200 位")
    @Schema(description = "备注")
    private String remark;
}
