package com.shop.admin.stats.vo;

import com.shop.admin.common.enums.OrderStatus;
import com.shop.admin.stats.model.OrderStatusStatDO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 订单状态分布视图对象。
 *
 * @author shop-admin
 */
@Getter
@Setter
@Schema(description = "订单状态分布")
public class OrderStatusStatVO {

    @Schema(description = "订单状态码", example = "1")
    private Integer status;

    @Schema(description = "订单状态名称", example = "待发货")
    private String statusName;

    @Schema(description = "该状态订单数", example = "12")
    private Long orderCount;

    @Schema(description = "该状态商品总额", example = "12800.00")
    private BigDecimal totalAmount;

    @Schema(description = "该状态应付金额", example = "12800.00")
    private BigDecimal payAmount;

    /**
     * 根据指定状态构造一个零值分布项。
     *
     * @param status 订单状态
     * @return 零值分布项
     */
    public static OrderStatusStatVO zero(OrderStatus status) {
        OrderStatusStatVO vo = new OrderStatusStatVO();
        vo.setStatus(status.getCode());
        vo.setStatusName(status.getDescription());
        vo.setOrderCount(0L);
        vo.setTotalAmount(BigDecimal.ZERO);
        vo.setPayAmount(BigDecimal.ZERO);
        return vo;
    }

    /**
     * 将数据对象转换为视图对象。
     *
     * @param statDO 状态统计数据对象
     * @return 状态分布视图对象
     */
    public static OrderStatusStatVO from(OrderStatusStatDO statDO) {
        OrderStatus status = OrderStatus.fromCode(statDO.getStatus());
        OrderStatusStatVO vo = new OrderStatusStatVO();
        vo.setStatus(status.getCode());
        vo.setStatusName(status.getDescription());
        vo.setOrderCount(statDO.getOrderCount() == null ? 0L : statDO.getOrderCount());
        vo.setTotalAmount(statDO.getTotalAmount() == null ? BigDecimal.ZERO : statDO.getTotalAmount());
        vo.setPayAmount(statDO.getPayAmount() == null ? BigDecimal.ZERO : statDO.getPayAmount());
        return vo;
    }
}
