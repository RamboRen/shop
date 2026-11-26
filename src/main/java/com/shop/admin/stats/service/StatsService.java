package com.shop.admin.stats.service;

import com.shop.admin.stats.dto.OrderStatsQueryRequest;
import com.shop.admin.stats.vo.CategoryStatVO;
import com.shop.admin.stats.vo.OrderStatsVO;
import com.shop.admin.stats.vo.SalesTrendVO;
import com.shop.admin.stats.vo.StatsOverviewVO;
import com.shop.admin.stats.vo.TopProductVO;

import java.util.List;

/**
 * 后台统计服务接口。
 *
 * @author shop-admin
 */
public interface StatsService {

    /**
     * 查询后台概览统计。
     *
     * @return 概览统计
     */
    StatsOverviewVO getOverview();

    /**
     * 查询销售趋势，自动补齐无订单日期。
     *
     * @param days 统计天数
     * @return 按日销售趋势
     */
    List<SalesTrendVO> getSalesTrend(int days);

    /**
     * 查询分类商品分布。
     *
     * @return 分类商品分布
     */
    List<CategoryStatVO> getCategoryDistribution();

    /**
     * 查询热销商品。
     *
     * @param limit 返回条数
     * @return 热销商品列表
     */
    List<TopProductVO> getTopProducts(int limit);

    /**
     * 查询订单统计。
     *
     * @param request 查询条件，支持日期范围
     * @return 订单统计
     */
    OrderStatsVO getOrderStats(OrderStatsQueryRequest request);
}
