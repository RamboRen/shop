package com.shop.admin.order.vo;

import com.shop.admin.order.entity.OrderDO;
import com.shop.admin.order.entity.OrderItemDO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 订单详情视图对象。
 *
 * @author shop-admin
 */
@Getter
@Setter
@Schema(description = "订单详情")
public class OrderDetailVO extends OrderVO {

    @Schema(description = "订单商品明细")
    private List<OrderItemVO> items;

    /**
     * 将订单及明细转换为详情视图对象。
     *
     * @param orderDO 订单数据对象
     * @param items   订单明细
     * @return 订单详情视图对象
     */
    public static OrderDetailVO from(OrderDO orderDO, List<OrderItemDO> items) {
        OrderDetailVO detailVO = new OrderDetailVO();
        detailVO.copyFrom(orderDO);
        detailVO.setItems(items.stream().map(OrderItemVO::from).toList());
        return detailVO;
    }
}
