package com.shop.admin.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 商品批量上下架请求。
 *
 * @author shop-admin
 */
@Getter
@Setter
@Schema(description = "商品批量上下架请求")
public class ProductBatchStatusRequest {

    @NotEmpty(message = "商品编号列表不能为空")
    @Size(max = 50, message = "单次最多操作 50 个商品")
    @Schema(description = "商品编号列表")
    private List<@NotNull(message = "商品编号不能为空")
            @Positive(message = "商品编号必须大于 0") Long> ids;

    @NotNull(message = "状态不能为空")
    @Min(value = 0, message = "状态只能是 0 或 1")
    @Max(value = 1, message = "状态只能是 0 或 1")
    @Schema(description = "状态，0 下架，1 上架", example = "1")
    private Integer status;
}
