package com.shop.admin.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * 用户分页查询请求。
 *
 * @author shop-admin
 */
@Getter
@Setter
@Schema(description = "用户分页查询请求")
public class UserQueryRequest {

    @Min(value = 1, message = "页码不能小于 1")
    @Schema(description = "页码", example = "1")
    private Integer pageNum = 1;

    @Min(value = 1, message = "每页条数不能小于 1")
    @Max(value = 100, message = "每页条数不能超过 100")
    @Schema(description = "每页条数", example = "10")
    private Integer pageSize = 10;

    @Size(max = 50, message = "关键字长度不能超过 50 位")
    @Schema(description = "用户编号、账号、昵称或手机号关键字")
    private String keyword;

    @Pattern(regexp = "^$|MALE|FEMALE|UNKNOWN", message = "性别只能是 MALE、FEMALE 或 UNKNOWN")
    @Schema(description = "性别", example = "MALE")
    private String gender;

    @Min(value = 0, message = "状态只能是 0 或 1")
    @Max(value = 1, message = "状态只能是 0 或 1")
    @Schema(description = "状态，0 禁用，1 启用")
    private Integer status;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Schema(description = "注册开始日期，格式 yyyy-MM-dd", example = "2026-11-01")
    private LocalDate startDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Schema(description = "注册结束日期，格式 yyyy-MM-dd", example = "2026-11-30")
    private LocalDate endDate;
}
