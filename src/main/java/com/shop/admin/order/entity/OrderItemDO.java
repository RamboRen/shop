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
 * 订单明细数据对象。
 *
 * @author shop-admin
 */
@Getter
@Setter
@TableName("shop_order_item")
public class OrderItemDO {

    /**
     * 主键。
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 订单编号。
     */
    private Long orderId;

    /**
     * 商品编号。
     */
    private Long productId;

    /**
     * 商品名称快照。
     */
    private String productName;

    /**
     * 商品图片快照。
     */
    private String productImage;

    /**
     * 商品单价。
     */
    private BigDecimal price;

    /**
     * 购买数量。
     */
    private Integer quantity;

    /**
     * 小计金额。
     */
    private BigDecimal totalAmount;

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
