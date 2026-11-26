package com.shop.admin.stats.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 热销商品数据对象。
 *
 * @author shop-admin
 */
@Getter
@Setter
public class TopProductDO {

    /**
     * 商品编号。
     */
    private Long productId;

    /**
     * 商品名称。
     */
    private String productName;

    /**
     * 销售价。
     */
    private BigDecimal price;

    /**
     * 累计销量。
     */
    private Long salesVolume;

    /**
     * 库存数量。
     */
    private Integer stock;
}
