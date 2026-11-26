package com.shop.admin.spec.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 修改商品规格请求。
 *
 * @author shop-admin
 */
@Getter
@Setter
@Schema(description = "修改商品规格请求")
public class SpecUpdateRequest {

    @NotBlank(message = "规格名称不能为空")
    @Size(max = 50, message = "规格名称长度不能超过 50 位")
    @Schema(description = "规格名称", example = "颜色")
    private String name;

    @NotEmpty(message = "规格值不能为空")
    @Size(max = 20, message = "规格值最多 20 个")
    @Schema(description = "规格值列表", example = "[\"黑色\",\"白色\"]")
    private List<@NotBlank(message = "规格值不能为空")
            @Size(max = 50, message = "单个规格值长度不能超过 50 位") String> values;

    @Min(value = 0, message = "状态只能是 0 或 1")
    @Max(value = 1, message = "状态只能是 0 或 1")
    @Schema(description = "状态，0 禁用，1 启用", example = "1")
    private Integer status;

    @Min(value = 0, message = "排序值不能小于 0")
    @Max(value = 9999, message = "排序值不能超过 9999")
    @Schema(description = "排序值，数值越小越靠前", example = "1")
    private Integer sort;

    @Size(max = 200, message = "备注长度不能超过 200 位")
    @Schema(description = "备注")
    private String remark;
}
