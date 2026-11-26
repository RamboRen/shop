package com.shop.admin.setting.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * 新增系统设置项请求。
 *
 * @author shop-admin
 */
@Getter
@Setter
@Schema(description = "新增系统设置项请求")
public class SettingCreateRequest {

    @NotBlank(message = "配置键不能为空")
    @Pattern(regexp = "^[a-zA-Z][a-zA-Z0-9_.]{1,63}$", message = "配置键需以字母开头，仅含字母、数字、点或下划线，长度 2 到 64 位")
    @Schema(description = "配置键", example = "site.name")
    private String configKey;

    @NotBlank(message = "配置名称不能为空")
    @Size(max = 100, message = "配置名称长度不能超过 100 位")
    @Schema(description = "配置显示名称", example = "站点名称")
    private String configName;

    @NotBlank(message = "配置分组不能为空")
    @Pattern(regexp = "BASIC|CONTACT|TRADE|PAYMENT", message = "分组只能是 BASIC、CONTACT、TRADE 或 PAYMENT")
    @Schema(description = "配置分组", example = "BASIC")
    private String configGroup;

    @Size(max = 2000, message = "配置值长度不能超过 2000 位")
    @Schema(description = "配置值", example = "云商城")
    private String configValue;

    @NotBlank(message = "值类型不能为空")
    @Pattern(regexp = "STRING|TEXT|NUMBER|BOOLEAN", message = "值类型只能是 STRING、TEXT、NUMBER 或 BOOLEAN")
    @Schema(description = "值类型", example = "STRING")
    private String valueType;

    @Min(value = 0, message = "排序值不能小于 0")
    @Max(value = 9999, message = "排序值不能超过 9999")
    @Schema(description = "排序值", example = "1")
    private Integer sort;

    @Size(max = 255, message = "备注长度不能超过 255 位")
    @Schema(description = "备注说明")
    private String remark;
}
