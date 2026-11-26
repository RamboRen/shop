package com.shop.admin.setting.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 按分组批量保存设置项请求。
 *
 * @author shop-admin
 */
@Getter
@Setter
@Schema(description = "按分组批量保存设置项请求")
public class SettingBatchSaveRequest {

    @Pattern(regexp = "BASIC|CONTACT|TRADE|PAYMENT", message = "分组只能是 BASIC、CONTACT、TRADE 或 PAYMENT")
    @Schema(description = "配置分组，用于校验批量保存的项都归属于该分组", example = "BASIC")
    private String configGroup;

    @Valid
    @NotEmpty(message = "设置项列表不能为空")
    @Schema(description = "设置项值列表")
    private List<SettingValueItem> items;
}
