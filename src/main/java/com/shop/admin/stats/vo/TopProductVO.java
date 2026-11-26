package com.shop.admin.stats.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 热销商品视图对象。
 *
 * @author shop-admin
 */
@Getter
@Setter
@Schema(description = "热销商品")
public class TopProductVO {

    @Schema(description = "商品编号", example = "1")
    private Long productId;

    @Schema(description = "商品名称", example = "无线蓝牙耳机")
    private String productName;

    @Schema(description = "销售价", example = "299.00")
    private BigDecimal price;

    @Schema(description = "累计销量", example = "120")
    private Long salesVolume;

    @Schema(description = "库存数量", example = "100")
    private Integer stock;

    /**
     * 构造热销商品视图对象。
     *
     * @param productId   商品编号
     * @param productName 商品名称
     * @param price       销售价
     * @param salesVolume 累计销量
     * @param stock       库存
     */
    public TopProductVO(Long productId, String productName, BigDecimal price,
                        Long salesVolume, Integer stock) {
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.salesVolume = salesVolume;
        this.stock = stock;
    }
}
