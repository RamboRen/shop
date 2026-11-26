package com.shop.admin.stats.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 销售趋势视图对象。
 *
 * @author shop-admin
 */
@Getter
@Setter
@Schema(description = "单日销售趋势")
public class SalesTrendVO {

    @Schema(description = "统计日期", example = "2026-11-09")
    private LocalDate saleDate;

    @Schema(description = "销售额", example = "3200.00")
    private BigDecimal salesAmount;

    @Schema(description = "订单数", example = "12")
    private Long orderCount;

    /**
     * 构造销售趋势视图对象。
     *
     * @param saleDate    统计日期
     * @param salesAmount 销售额
     * @param orderCount  订单数
     */
    public SalesTrendVO(LocalDate saleDate, BigDecimal salesAmount, Long orderCount) {
        this.saleDate = saleDate;
        this.salesAmount = salesAmount;
        this.orderCount = orderCount;
    }
}
