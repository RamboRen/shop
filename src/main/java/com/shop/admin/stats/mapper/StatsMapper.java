package com.shop.admin.stats.mapper;

import com.shop.admin.stats.model.CategoryStatDO;
import com.shop.admin.stats.model.OrderStatusStatDO;
import com.shop.admin.stats.model.PayTypeStatDO;
import com.shop.admin.stats.model.SalesTrendDO;
import com.shop.admin.stats.model.StatsOverviewDO;
import com.shop.admin.stats.model.TopProductDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 后台统计数据访问接口。
 *
 * @author shop-admin
 */
@Mapper
public interface StatsMapper {

    /**
     * 查询概览统计。
     *
     * @param lowStockThreshold 低库存阈值
     * @return 概览统计
     */
    StatsOverviewDO selectOverview(@Param("lowStockThreshold") int lowStockThreshold);

    /**
     * 查询销售趋势。
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 按日销售趋势
     */
    List<SalesTrendDO> selectSalesTrend(@Param("startTime") LocalDateTime startTime,
                                        @Param("endTime") LocalDateTime endTime);

    /**
     * 查询分类商品分布。
     *
     * @return 分类商品分布
     */
    List<CategoryStatDO> selectCategoryDistribution();

    /**
     * 查询热销商品。
     *
     * @param limit 返回条数
     * @return 热销商品列表
     */
    List<TopProductDO> selectTopProducts(@Param("limit") int limit);

    /**
     * 按订单状态聚合统计。
     *
     * @param startTime 起始时间，含
     * @param endTime   结束时间，不含
     * @return 各状态统计数据
     */
    List<OrderStatusStatDO> selectOrderStatusStats(@Param("startTime") LocalDateTime startTime,
                                                   @Param("endTime") LocalDateTime endTime);

    /**
     * 按支付方式聚合统计。
     *
     * @param startTime 起始时间，含
     * @param endTime   结束时间，不含
     * @return 各支付方式统计数据
     */
    List<PayTypeStatDO> selectPayTypeStats(@Param("startTime") LocalDateTime startTime,
                                           @Param("endTime") LocalDateTime endTime);
}
