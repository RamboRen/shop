package com.shop.admin.setting.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * 系统设置分页查询请求。
 *
 * @author shop-admin
 */
@Getter
@Setter
@Schema(description = "系统设置分页查询请求")
public class SettingQueryRequest {

    @Min(value = 1, message = "页码不能小于 1")
    @Schema(description = "页码", example = "1")
    private Integer pageNum = 1;

    @Min(value = 1, message = "每页条数不能小于 1")
    @Max(value = 100, message = "每页条数不能超过 100")
    @Schema(description = "每页条数", example = "10")
    private Integer pageSize = 10;

    @Size(max = 100, message = "关键字长度不能超过 100 位")
    @Schema(description = "配置键、配置名称关键字")
    private String keyword;

    @Pattern(regexp = "^$|BASIC|CONTACT|TRADE|PAYMENT", message = "分组只能是 BASIC、CONTACT、TRADE 或 PAYMENT")
    @Schema(description = "配置分组", example = "BASIC")
    private String configGroup;
}
