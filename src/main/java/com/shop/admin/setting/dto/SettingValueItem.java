package com.shop.admin.setting.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * 批量保存设置时的单个配置项值。
 *
 * @author shop-admin
 */
@Getter
@Setter
@Schema(description = "批量保存的设置项值")
public class SettingValueItem {

    @NotBlank(message = "配置键不能为空")
    @Size(max = 64, message = "配置键长度不能超过 64 位")
    @Schema(description = "配置键", example = "site.name")
    private String configKey;

    @Size(max = 2000, message = "配置值长度不能超过 2000 位")
    @Schema(description = "配置值", example = "云商城")
    private String configValue;
}
