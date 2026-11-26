package com.shop.admin.stats.controller;

import com.shop.admin.common.api.ApiResult;
import com.shop.admin.stats.dto.OrderStatsQueryRequest;
import com.shop.admin.stats.service.StatsService;
import com.shop.admin.stats.vo.CategoryStatVO;
import com.shop.admin.stats.vo.OrderStatsVO;
import com.shop.admin.stats.vo.SalesTrendVO;
import com.shop.admin.stats.vo.StatsOverviewVO;
import com.shop.admin.stats.vo.TopProductVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 后台数据统计接口。
 *
 * @author shop-admin
 */
@RestController
@RequestMapping("/api/admin/stats")
@RequiredArgsConstructor
@PreAuthorize("hasRole('SUPER_ADMIN') or hasAuthority('stats:view')")
@Tag(name = "后台数据统计", description = "概览、销售趋势、分类分布与热销商品")
@SecurityRequirement(name = "bearerAuth")
public class StatsController {

    private final StatsService statsService;

    /**
     * 查询后台概览统计。
     *
     * @return 概览统计
     */
    @GetMapping("/overview")
    @Operation(summary = "查询后台概览统计")
    public ApiResult<StatsOverviewVO> getOverview() {
        return ApiResult.success(statsService.getOverview());
    }

    /**
     * 查询销售趋势。
     *
     * @param days 统计天数
     * @return 按日销售趋势
     */
    @GetMapping("/sales-trend")
    @Operation(summary = "查询销售趋势", description = "返回连续天数的销售数据，无订单日期自动补零")
    public ApiResult<List<SalesTrendVO>> getSalesTrend(
            @Parameter(description = "统计天数，取值 7 到 90")
            @RequestParam(defaultValue = "7") int days) {
        return ApiResult.success(statsService.getSalesTrend(days));
    }

    /**
     * 查询分类商品分布。
     *
     * @return 分类商品分布
     */
    @GetMapping("/category-distribution")
    @Operation(summary = "查询分类商品分布")
    public ApiResult<List<CategoryStatVO>> getCategoryDistribution() {
        return ApiResult.success(statsService.getCategoryDistribution());
    }

    /**
     * 查询热销商品。
     *
     * @param limit 返回条数
     * @return 热销商品列表
     */
    @GetMapping("/top-products")
    @Operation(summary = "查询热销商品")
    public ApiResult<List<TopProductVO>> getTopProducts(
            @Parameter(description = "返回条数，取值 3 到 20")
            @RequestParam(defaultValue = "5") int limit) {
        return ApiResult.success(statsService.getTopProducts(limit));
    }

    /**
     * 查询订单统计。
     *
     * @param request 查询条件，支持日期范围
     * @return 订单统计
     */
    @GetMapping("/orders")
    @Operation(summary = "查询订单统计", description = "返回订单核心指标、各状态分布与支付方式分布，支持日期范围筛选")
    public ApiResult<OrderStatsVO> getOrderStats(@Valid @ParameterObject OrderStatsQueryRequest request) {
        return ApiResult.success(statsService.getOrderStats(request));
    }
}
