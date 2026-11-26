package com.shop.admin.stats.service.impl;

import com.shop.admin.common.constant.CommonConstants;
import com.shop.admin.common.enums.OrderStatus;
import com.shop.admin.common.exception.BusinessException;
import com.shop.admin.stats.dto.OrderStatsQueryRequest;
import com.shop.admin.stats.mapper.StatsMapper;
import com.shop.admin.stats.model.OrderStatusStatDO;
import com.shop.admin.stats.model.PayTypeStatDO;
import com.shop.admin.stats.model.SalesTrendDO;
import com.shop.admin.stats.model.StatsOverviewDO;
import com.shop.admin.stats.model.TopProductDO;
import com.shop.admin.stats.service.StatsService;
import com.shop.admin.stats.vo.CategoryStatVO;
import com.shop.admin.stats.vo.OrderStatsVO;
import com.shop.admin.stats.vo.OrderStatusStatVO;
import com.shop.admin.stats.vo.PayTypeStatVO;
import com.shop.admin.stats.vo.SalesTrendVO;
import com.shop.admin.stats.vo.StatsOverviewVO;
import com.shop.admin.stats.vo.TopProductVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 后台统计服务实现。
 *
 * @author shop-admin
 */
@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    private static final int MIN_TREND_DAYS = 7;

    private static final int MAX_TREND_DAYS = 90;

    private static final int MIN_TOP_LIMIT = 3;

    private static final int MAX_TOP_LIMIT = 20;

    private final StatsMapper statsMapper;

    @Override
    public StatsOverviewVO getOverview() {
        StatsOverviewDO overviewDO = statsMapper.selectOverview(CommonConstants.LOW_STOCK_THRESHOLD);
        if (overviewDO == null) {
            overviewDO = emptyOverview();
        }
        return StatsOverviewVO.from(overviewDO);
    }

    @Override
    public List<SalesTrendVO> getSalesTrend(int days) {
        if (days < MIN_TREND_DAYS || days > MAX_TREND_DAYS) {
            throw new BusinessException(400,
                    "统计天数必须在 " + MIN_TREND_DAYS + " 到 " + MAX_TREND_DAYS + " 之间");
        }
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days - 1L);
        List<SalesTrendDO> trendDOS = statsMapper.selectSalesTrend(
                startDate.atStartOfDay(), endDate.plusDays(1).atStartOfDay());
        Map<LocalDate, SalesTrendDO> trendMap = new HashMap<>(trendDOS.size());
        for (SalesTrendDO trendDO : trendDOS) {
            trendMap.put(trendDO.getSaleDate(), trendDO);
        }
        List<SalesTrendVO> trendVOS = new ArrayList<>(days);
        for (int index = 0; index < days; index++) {
            LocalDate saleDate = startDate.plusDays(index);
            SalesTrendDO trendDO = trendMap.get(saleDate);
            BigDecimal salesAmount = trendDO == null ? BigDecimal.ZERO : trendDO.getSalesAmount();
            Long orderCount = trendDO == null ? 0L : trendDO.getOrderCount();
            trendVOS.add(new SalesTrendVO(saleDate, salesAmount, orderCount));
        }
        return trendVOS;
    }

    @Override
    public List<CategoryStatVO> getCategoryDistribution() {
        return statsMapper.selectCategoryDistribution().stream()
                .map(categoryStatDO -> new CategoryStatVO(
                        categoryStatDO.getCategoryName(), categoryStatDO.getProductCount()))
                .toList();
    }

    @Override
    public List<TopProductVO> getTopProducts(int limit) {
        if (limit < MIN_TOP_LIMIT || limit > MAX_TOP_LIMIT) {
            throw new BusinessException(400,
                    "热销商品条数必须在 " + MIN_TOP_LIMIT + " 到 " + MAX_TOP_LIMIT + " 之间");
        }
        return statsMapper.selectTopProducts(limit).stream()
                .map(this::convertTopProduct)
                .toList();
    }

    @Override
    public OrderStatsVO getOrderStats(OrderStatsQueryRequest request) {
        LocalDateTime startTime = null;
        LocalDateTime endTime = null;
        if (request.getStartDate() != null && request.getEndDate() != null
                && request.getStartDate().isAfter(request.getEndDate())) {
            throw new BusinessException(400, "开始日期不能晚于结束日期");
        }
        if (request.getStartDate() != null) {
            startTime = request.getStartDate().atStartOfDay();
        }
        if (request.getEndDate() != null) {
            endTime = request.getEndDate().plusDays(1).atStartOfDay();
        }

        List<OrderStatusStatDO> statusStats = statsMapper.selectOrderStatusStats(startTime, endTime);
        List<PayTypeStatDO> payTypeStats = statsMapper.selectPayTypeStats(startTime, endTime);

        Map<OrderStatus, OrderStatusStatDO> statusMap = new EnumMap<>(OrderStatus.class);
        for (OrderStatusStatDO statDO : statusStats) {
            statusMap.put(OrderStatus.fromCode(statDO.getStatus()), statDO);
        }
        List<OrderStatusStatVO> statusDistribution = new ArrayList<>(OrderStatus.values().length);
        for (OrderStatus status : OrderStatus.values()) {
            OrderStatusStatDO statDO = statusMap.get(status);
            statusDistribution.add(statDO == null
                    ? OrderStatusStatVO.zero(status) : OrderStatusStatVO.from(statDO));
        }

        long totalOrderCount = 0L;
        long validOrderCount = 0L;
        BigDecimal totalProductAmount = BigDecimal.ZERO;
        BigDecimal totalPayAmount = BigDecimal.ZERO;
        for (OrderStatusStatVO statVO : statusDistribution) {
            long count = statVO.getOrderCount();
            totalOrderCount += count;
            if (isEffectiveStatus(statVO.getStatus())) {
                validOrderCount += count;
                totalProductAmount = totalProductAmount.add(statVO.getTotalAmount());
                totalPayAmount = totalPayAmount.add(statVO.getPayAmount());
            }
        }
        BigDecimal averageOrderAmount = validOrderCount == 0 ? BigDecimal.ZERO
                : totalPayAmount.divide(BigDecimal.valueOf(validOrderCount), 2, RoundingMode.HALF_UP);

        List<PayTypeStatVO> payTypeDistribution = payTypeStats.stream()
                .map(PayTypeStatVO::from)
                .toList();

        OrderStatsVO vo = new OrderStatsVO();
        vo.setTotalOrderCount(totalOrderCount);
        vo.setValidOrderCount(validOrderCount);
        vo.setTotalProductAmount(totalProductAmount);
        vo.setTotalPayAmount(totalPayAmount);
        vo.setAverageOrderAmount(averageOrderAmount);
        vo.setStatusDistribution(statusDistribution);
        vo.setPayTypeDistribution(payTypeDistribution);
        return vo;
    }

    private boolean isEffectiveStatus(int statusCode) {
        OrderStatus status = OrderStatus.fromCode(statusCode);
        return status == OrderStatus.PAID
                || status == OrderStatus.SHIPPED
                || status == OrderStatus.COMPLETED;
    }

    private TopProductVO convertTopProduct(TopProductDO productDO) {
        return new TopProductVO(productDO.getProductId(), productDO.getProductName(),
                productDO.getPrice(), productDO.getSalesVolume(), productDO.getStock());
    }

    private StatsOverviewDO emptyOverview() {
        StatsOverviewDO overviewDO = new StatsOverviewDO();
        overviewDO.setTotalSalesAmount(BigDecimal.ZERO);
        overviewDO.setTodaySalesAmount(BigDecimal.ZERO);
        overviewDO.setTotalOrderCount(0L);
        overviewDO.setTodayOrderCount(0L);
        overviewDO.setProductCount(0L);
        overviewDO.setOnSaleProductCount(0L);
        overviewDO.setCategoryCount(0L);
        overviewDO.setGalleryCount(0L);
        overviewDO.setAdminCount(0L);
        overviewDO.setLowStockProductCount(0L);
        overviewDO.setTodayNewProductCount(0L);
        overviewDO.setTotalSalesVolume(0L);
        return overviewDO;
    }
}
