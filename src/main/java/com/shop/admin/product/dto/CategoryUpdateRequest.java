package com.shop.admin.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * 修改商品分类请求。
 *
 * @author shop-admin
 */
@Getter
@Setter
@Schema(description = "修改商品分类请求")
public class CategoryUpdateRequest {

    @NotBlank(message = "分类名称不能为空")
    @Size(min = 2, max = 30, message = "分类名称长度必须为 2 到 30 位")
    @Schema(description = "分类名称", example = "数码家电")
    private String name;

    @NotNull(message = "排序值不能为空")
    @Min(value = 0, message = "排序值不能小于 0")
    @Max(value = 9999, message = "排序值不能超过 9999")
    @Schema(description = "排序值，数值越小越靠前", example = "1")
    private Integer sort;

    @NotNull(message = "状态不能为空")
    @Min(value = 0, message = "状态只能是 0 或 1")
    @Max(value = 1, message = "状态只能是 0 或 1")
    @Schema(description = "状态，0 禁用，1 启用", example = "1")
    private Integer status;
}
