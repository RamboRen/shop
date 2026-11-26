package com.shop.admin.order.vo;

import com.shop.admin.order.entity.OrderItemDO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 订单明细视图对象。
 *
 * @author shop-admin
 */
@Getter
@Setter
@Schema(description = "订单商品明细")
public class OrderItemVO {

    @Schema(description = "明细编号", example = "1")
    private Long id;

    @Schema(description = "商品编号", example = "1")
    private Long productId;

    @Schema(description = "商品名称", example = "无线蓝牙耳机")
    private String productName;

    @Schema(description = "商品图片")
    private String productImage;

    @Schema(description = "商品单价", example = "299.00")
    private BigDecimal price;

    @Schema(description = "购买数量", example = "1")
    private Integer quantity;

    @Schema(description = "小计金额", example = "299.00")
    private BigDecimal totalAmount;

    /**
     * 将订单明细数据对象转换为视图对象。
     *
     * @param itemDO 订单明细数据对象
     * @return 订单明细视图对象
     */
    public static OrderItemVO from(OrderItemDO itemDO) {
        OrderItemVO itemVO = new OrderItemVO();
        itemVO.setId(itemDO.getId());
        itemVO.setProductId(itemDO.getProductId());
        itemVO.setProductName(itemDO.getProductName());
        itemVO.setProductImage(itemDO.getProductImage());
        itemVO.setPrice(itemDO.getPrice());
        itemVO.setQuantity(itemDO.getQuantity());
        itemVO.setTotalAmount(itemDO.getTotalAmount());
        return itemVO;
    }
}
