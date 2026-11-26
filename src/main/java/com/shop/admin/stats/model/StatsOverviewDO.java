package com.shop.admin.stats.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 后台概览统计数据对象。
 *
 * @author shop-admin
 */
@Getter
@Setter
public class StatsOverviewDO {

    /**
     * 累计销售额。
     */
    private BigDecimal totalSalesAmount;

    /**
     * 今日销售额。
     */
    private BigDecimal todaySalesAmount;

    /**
     * 累计有效订单数。
     */
    private Long totalOrderCount;

    /**
     * 今日有效订单数。
     */
    private Long todayOrderCount;

    /**
     * 商品总数。
     */
    private Long productCount;

    /**
     * 上架商品数。
     */
    private Long onSaleProductCount;

    /**
     * 分类总数。
     */
    private Long categoryCount;

    /**
     * 图库图片总数。
     */
    private Long galleryCount;

    /**
     * 管理员总数。
     */
    private Long adminCount;

    /**
     * 低库存商品数。
     */
    private Long lowStockProductCount;

    /**
     * 今日新增商品数。
     */
    private Long todayNewProductCount;

    /**
     * 累计销量。
     */
    private Long totalSalesVolume;
}
