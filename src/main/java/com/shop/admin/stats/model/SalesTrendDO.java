package com.shop.admin.stats.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 销售趋势数据对象。
 *
 * @author shop-admin
 */
@Getter
@Setter
public class SalesTrendDO {

    /**
     * 统计日期。
     */
    private LocalDate saleDate;

    /**
     * 销售额。
     */
    private BigDecimal salesAmount;

    /**
     * 订单数。
     */
    private Long orderCount;
}
