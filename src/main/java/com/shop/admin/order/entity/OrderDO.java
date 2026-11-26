package com.shop.admin.order.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.shop.admin.common.constant.CommonConstants;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单数据对象。
 *
 * @author shop-admin
 */
@Getter
@Setter
@TableName("shop_order")
public class OrderDO {

    /**
     * 主键。
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 订单号。
     */
    private String orderNo;

    /**
     * 会员编号。
     */
    private Long userId;

    /**
     * 收货人。
     */
    private String receiverName;

    /**
     * 收货手机号。
     */
    private String receiverPhone;

    /**
     * 收货地址。
     */
    private String receiverAddress;

    /**
     * 商品总额。
     */
    private BigDecimal totalAmount;

    /**
     * 运费。
     */
    private BigDecimal freightAmount;

    /**
     * 优惠金额。
     */
    private BigDecimal discountAmount;

    /**
     * 应付金额。
     */
    private BigDecimal payAmount;

    /**
     * 支付方式：WECHAT/ALIPAY/BALANCE。
     */
    private String payType;

    /**
     * 订单状态：0 待付款，1 待发货，2 待收货，3 已完成，4 已取消。
     */
    private Integer status;

    /**
     * 商品种类数。
     */
    private Integer itemCount;

    /**
     * 商品总数量。
     */
    private Integer totalQuantity;

    /**
     * 支付时间。
     */
    private LocalDateTime payTime;

    /**
     * 发货时间。
     */
    private LocalDateTime shipTime;

    /**
     * 完成时间。
     */
    private LocalDateTime finishTime;

    /**
     * 取消时间。
     */
    private LocalDateTime cancelTime;

    /**
     * 订单备注。
     */
    private String remark;

    /**
     * 创建时间。
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间。
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 逻辑删除标识，0 未删除，1 已删除。
     */
    @TableLogic
    private Integer deleted = CommonConstants.NOT_DELETED;
}
