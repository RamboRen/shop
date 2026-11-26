package com.shop.admin.order.vo;

import com.shop.admin.common.enums.OrderPayType;
import com.shop.admin.common.enums.OrderStatus;
import com.shop.admin.order.entity.OrderDO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单列表视图对象。
 *
 * @author shop-admin
 */
@Getter
@Setter
@Schema(description = "订单信息")
public class OrderVO {

    @Schema(description = "订单编号", example = "1")
    private Long id;

    @Schema(description = "订单号", example = "SO20261109001")
    private String orderNo;

    @Schema(description = "会员编号", example = "1001")
    private Long userId;

    @Schema(description = "收货人", example = "张伟")
    private String receiverName;

    @Schema(description = "收货手机号", example = "13800000001")
    private String receiverPhone;

    @Schema(description = "收货地址")
    private String receiverAddress;

    @Schema(description = "商品总额")
    private BigDecimal totalAmount;

    @Schema(description = "运费")
    private BigDecimal freightAmount;

    @Schema(description = "优惠金额")
    private BigDecimal discountAmount;

    @Schema(description = "应付金额")
    private BigDecimal payAmount;

    @Schema(description = "支付方式编码", example = "WECHAT")
    private String payType;

    @Schema(description = "支付方式名称", example = "微信支付")
    private String payTypeName;

    @Schema(description = "订单状态", example = "1")
    private Integer status;

    @Schema(description = "订单状态名称", example = "待发货")
    private String statusName;

    @Schema(description = "商品种类数", example = "1")
    private Integer itemCount;

    @Schema(description = "商品总数量", example = "1")
    private Integer totalQuantity;

    @Schema(description = "支付时间")
    private LocalDateTime payTime;

    @Schema(description = "发货时间")
    private LocalDateTime shipTime;

    @Schema(description = "完成时间")
    private LocalDateTime finishTime;

    @Schema(description = "取消时间")
    private LocalDateTime cancelTime;

    @Schema(description = "订单备注")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    /**
     * 将订单数据对象转换为列表视图对象。
     *
     * @param orderDO 订单数据对象
     * @return 订单视图对象
     */
    public static OrderVO from(OrderDO orderDO) {
        OrderVO orderVO = new OrderVO();
        orderVO.copyFrom(orderDO);
        return orderVO;
    }

    /**
     * 复制订单基础字段。
     *
     * @param orderDO 订单数据对象
     */
    protected void copyFrom(OrderDO orderDO) {
        setId(orderDO.getId());
        setOrderNo(orderDO.getOrderNo());
        setUserId(orderDO.getUserId());
        setReceiverName(orderDO.getReceiverName());
        setReceiverPhone(orderDO.getReceiverPhone());
        setReceiverAddress(orderDO.getReceiverAddress());
        setTotalAmount(orderDO.getTotalAmount());
        setFreightAmount(orderDO.getFreightAmount());
        setDiscountAmount(orderDO.getDiscountAmount());
        setPayAmount(orderDO.getPayAmount());
        setPayType(orderDO.getPayType());
        setPayTypeName(OrderPayType.describe(orderDO.getPayType()));
        setStatus(orderDO.getStatus());
        setStatusName(OrderStatus.fromCode(orderDO.getStatus()).getDescription());
        setItemCount(orderDO.getItemCount());
        setTotalQuantity(orderDO.getTotalQuantity());
        setPayTime(orderDO.getPayTime());
        setShipTime(orderDO.getShipTime());
        setFinishTime(orderDO.getFinishTime());
        setCancelTime(orderDO.getCancelTime());
        setRemark(orderDO.getRemark());
        setCreateTime(orderDO.getCreateTime());
        setUpdateTime(orderDO.getUpdateTime());
    }
}
