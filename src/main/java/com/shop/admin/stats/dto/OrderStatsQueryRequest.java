package com.shop.admin.stats.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * 订单统计查询请求。
 *
 * @author shop-admin
 */
@Getter
@Setter
@Schema(description = "订单统计查询请求")
public class OrderStatsQueryRequest {

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Schema(description = "统计开始日期，格式 yyyy-MM-dd，不含则不限制", example = "2026-11-01")
    private LocalDate startDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Schema(description = "统计结束日期，格式 yyyy-MM-dd，含当日，不含则不限制", example = "2026-11-30")
    private LocalDate endDate;
}
