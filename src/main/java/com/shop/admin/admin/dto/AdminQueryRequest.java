package com.shop.admin.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * 管理员分页查询请求。
 *
 * @author shop-admin
 */
@Getter
@Setter
@Schema(description = "管理员分页查询请求")
public class AdminQueryRequest {

    @Min(value = 1, message = "页码不能小于 1")
    @Schema(description = "页码", example = "1")
    private Integer pageNum = 1;

    @Min(value = 1, message = "每页条数不能小于 1")
    @Max(value = 100, message = "每页条数不能超过 100")
    @Schema(description = "每页条数", example = "10")
    private Integer pageSize = 10;

    @Size(max = 50, message = "关键字长度不能超过 50 位")
    @Schema(description = "用户名或昵称关键字")
    private String keyword;

    @Pattern(regexp = "^$|SUPER_ADMIN|ADMIN", message = "角色只能是 SUPER_ADMIN 或 ADMIN")
    @Schema(description = "角色编码")
    private String roleCode;

    @Min(value = 0, message = "状态只能是 0 或 1")
    @Max(value = 1, message = "状态只能是 0 或 1")
    @Schema(description = "状态，0 禁用，1 启用")
    private Integer status;
}
