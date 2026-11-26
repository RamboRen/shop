package com.shop.admin.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**
 * 新增商品请求。
 *
 * @author shop-admin
 */
@Getter
@Setter
@Schema(description = "新增商品请求")
public class ProductCreateRequest {

    @Pattern(regexp = "^$|^[A-Za-z0-9_-]{4,32}$",
            message = "商品编码必须为 4 到 32 位字母、数字、下划线或中划线")
    @Schema(description = "商品编码，留空时由系统生成", example = "SP20260001")
    private String productCode;

    @NotNull(message = "分类不能为空")
    @Positive(message = "分类编号必须大于 0")
    @Schema(description = "分类编号", example = "1")
    private Long categoryId;

    @NotBlank(message = "商品名称不能为空")
    @Size(max = 100, message = "商品名称长度不能超过 100 位")
    @Schema(description = "商品名称", example = "无线蓝牙耳机")
    private String name;

    @Size(max = 150, message = "副标题长度不能超过 150 位")
    @Schema(description = "商品副标题", example = "主动降噪，续航 30 小时")
    private String subtitle;

    @NotBlank(message = "商品主图不能为空")
    @Size(max = 255, message = "商品主图地址长度不能超过 255 位")
    @Schema(description = "商品主图地址", example = "/uploads/demo/product-1.jpg")
    private String mainImage;

    @Size(max = 8, message = "商品附图最多 8 张")
    @Schema(description = "商品附图地址列表")
    private List<@NotBlank(message = "附图地址不能为空")
            @Size(max = 255, message = "附图地址长度不能超过 255 位") String> imageUrls;

    @NotNull(message = "销售价不能为空")
    @DecimalMin(value = "0.01", message = "销售价不能小于 0.01")
    @Digits(integer = 8, fraction = 2, message = "销售价格式不正确")
    @Schema(description = "销售价", example = "299.00")
    private BigDecimal price;

    @DecimalMin(value = "0.01", message = "原价不能小于 0.01")
    @Digits(integer = 8, fraction = 2, message = "原价格式不正确")
    @Schema(description = "划线原价", example = "399.00")
    private BigDecimal originalPrice;

    @NotNull(message = "库存不能为空")
    @Min(value = 0, message = "库存不能小于 0")
    @Max(value = 999999, message = "库存不能超过 999999")
    @Schema(description = "库存数量", example = "100")
    private Integer stock;

    @Min(value = 0, message = "状态只能是 0 或 1")
    @Max(value = 1, message = "状态只能是 0 或 1")
    @Schema(description = "状态，0 下架，1 上架", example = "1")
    private Integer status;

    @Min(value = 0, message = "排序值不能小于 0")
    @Max(value = 9999, message = "排序值不能超过 9999")
    @Schema(description = "排序值，数值越小越靠前", example = "1")
    private Integer sort;

    @Size(max = 5000, message = "商品详情长度不能超过 5000 位")
    @Schema(description = "商品详情")
    private String detail;
}
