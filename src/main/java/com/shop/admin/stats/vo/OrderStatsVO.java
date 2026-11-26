package com.shop.admin.stats.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**
 * 订单统计视图对象，包含核心指标、状态分布与支付方式分布。
 *
 * @author shop-admin
 */
@Getter
@Setter
@Schema(description = "订单统计")
public class OrderStatsVO {

    @Schema(description = "订单总数（不含逻辑删除）", example = "320")
    private Long totalOrderCount;

    @Schema(description = "有效订单数（待发货/待收货/已完成）", example = "298")
    private Long validOrderCount;

    @Schema(description = "有效订单商品总额", example = "128900.00")
    private BigDecimal totalProductAmount;

    @Schema(description = "有效订单应付金额", example = "128900.00")
    private BigDecimal totalPayAmount;

    @Schema(description = "客单价（有效订单应付金额 / 有效订单数）", example = "432.55")
    private BigDecimal averageOrderAmount;

    @Schema(description = "各状态订单分布，固定包含全部 5 种状态")
    private List<OrderStatusStatVO> statusDistribution;

    @Schema(description = "各支付方式订单分布")
    private List<PayTypeStatVO> payTypeDistribution;
}
