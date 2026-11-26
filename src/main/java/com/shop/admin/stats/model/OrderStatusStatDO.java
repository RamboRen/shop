package com.shop.admin.stats.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 按订单状态聚合的统计数据对象。
 *
 * @author shop-admin
 */
@Getter
@Setter
public class OrderStatusStatDO {

    /**
     * 订单状态码。
     */
    private Integer status;

    /**
     * 订单数量。
     */
    private Long orderCount;

    /**
     * 商品总额合计。
     */
    private BigDecimal totalAmount;

    /**
     * 应付金额合计。
     */
    private BigDecimal payAmount;
}
