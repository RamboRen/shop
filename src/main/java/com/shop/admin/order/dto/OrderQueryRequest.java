package com.shop.admin.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * 订单分页查询请求。
 *
 * @author shop-admin
 */
@Getter
@Setter
@Schema(description = "订单分页查询请求")
public class OrderQueryRequest {

    @Min(value = 1, message = "页码不能小于 1")
    @Schema(description = "页码", example = "1")
    private Integer pageNum = 1;

    @Min(value = 1, message = "每页条数不能小于 1")
    @Max(value = 100, message = "每页条数不能超过 100")
    @Schema(description = "每页条数", example = "10")
    private Integer pageSize = 10;

    @Size(max = 50, message = "关键字长度不能超过 50 位")
    @Schema(description = "订单号、收货人或收货手机号关键字")
    private String keyword;

    @Min(value = 0, message = "订单状态只能是 0 到 4")
    @Max(value = 4, message = "订单状态只能是 0 到 4")
    @Schema(description = "订单状态：0 待付款，1 待发货，2 待收货，3 已完成，4 已取消")
    private Integer status;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Schema(description = "创建开始日期，格式 yyyy-MM-dd", example = "2026-11-01")
    private LocalDate startDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Schema(description = "创建结束日期，格式 yyyy-MM-dd", example = "2026-11-30")
    private LocalDate endDate;
}
