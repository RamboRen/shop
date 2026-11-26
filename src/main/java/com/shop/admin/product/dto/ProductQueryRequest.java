package com.shop.admin.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 商品分页查询请求。
 *
 * @author shop-admin
 */
@Getter
@Setter
@Schema(description = "商品分页查询请求")
public class ProductQueryRequest {

    @Min(value = 1, message = "页码不能小于 1")
    @Schema(description = "页码", example = "1")
    private Integer pageNum = 1;

    @Min(value = 1, message = "每页条数不能小于 1")
    @Max(value = 100, message = "每页条数不能超过 100")
    @Schema(description = "每页条数", example = "10")
    private Integer pageSize = 10;

    @Size(max = 100, message = "关键字长度不能超过 100 位")
    @Schema(description = "商品名称或商品编码关键字")
    private String keyword;

    @Positive(message = "分类编号必须大于 0")
    @Schema(description = "分类编号")
    private Long categoryId;

    @Min(value = 0, message = "状态只能是 0 或 1")
    @Max(value = 1, message = "状态只能是 0 或 1")
    @Schema(description = "状态，0 下架，1 上架")
    private Integer status;

    @DecimalMin(value = "0.00", message = "最低价格不能小于 0")
    @Schema(description = "最低价格")
    private BigDecimal minPrice;

    @DecimalMin(value = "0.00", message = "最高价格不能小于 0")
    @Schema(description = "最高价格")
    private BigDecimal maxPrice;
}
