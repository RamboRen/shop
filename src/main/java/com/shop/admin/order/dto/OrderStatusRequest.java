package com.shop.admin.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * 订单状态修改请求。
 *
 * @author shop-admin
 */
@Getter
@Setter
@Schema(description = "订单状态修改请求")
public class OrderStatusRequest {

    @NotNull(message = "订单状态不能为空")
    @Min(value = 0, message = "订单状态只能是 0 到 4")
    @Max(value = 4, message = "订单状态只能是 0 到 4")
    @Schema(description = "目标状态：0 待付款，1 待发货，2 待收货，3 已完成，4 已取消", example = "1")
    private Integer status;
}
