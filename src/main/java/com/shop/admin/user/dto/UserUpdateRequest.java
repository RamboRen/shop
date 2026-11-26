package com.shop.admin.user.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * 修改用户请求。登录账号不允许修改。
 *
 * @author shop-admin
 */
@Getter
@Setter
@Schema(description = "修改用户请求")
public class UserUpdateRequest {

    @NotBlank(message = "昵称不能为空")
    @Size(max = 50, message = "昵称长度不能超过 50 位")
    @Schema(description = "昵称", example = "张三")
    private String nickname;

    @Pattern(regexp = "^$|^1[3-9]\\d{9}$", message = "手机号格式不正确")
    @Schema(description = "手机号", example = "13800138000")
    private String phone;

    @Email(message = "邮箱格式不正确")
    @Size(max = 100, message = "邮箱长度不能超过 100 位")
    @Schema(description = "邮箱", example = "user@example.com")
    private String email;

    @Size(max = 255, message = "头像地址长度不能超过 255 位")
    @Schema(description = "头像地址")
    private String avatar;

    @Pattern(regexp = "^$|UNKNOWN|MALE|FEMALE", message = "性别只能是 UNKNOWN、MALE 或 FEMALE")
    @Schema(description = "性别，UNKNOWN/MALE/FEMALE", example = "MALE")
    private String gender;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "出生日期，格式 yyyy-MM-dd", example = "1995-06-01")
    private LocalDate birthday;

    @Size(max = 200, message = "备注长度不能超过 200 位")
    @Schema(description = "备注")
    private String remark;
}
