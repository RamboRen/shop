package com.shop.admin.coupon.entity;

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
 * 优惠券数据对象。
 *
 * @author shop-admin
 */
@Getter
@Setter
@TableName("shop_coupon")
public class CouponDO {

    /**
     * 主键。
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 优惠券编码。
     */
    private String couponCode;

    /**
     * 优惠券名称。
     */
    private String name;

    /**
     * 类型：AMOUNT 满减券，DISCOUNT 折扣券。
     */
    private String couponType;

    /**
     * 满减金额或折扣值，折扣券以 8.5 表示 8.5 折。
     */
    private BigDecimal discountValue;

    /**
     * 使用门槛，0 表示无门槛。
     */
    private BigDecimal thresholdAmount;

    /**
     * 发行总量。
     */
    private Integer totalCount;

    /**
     * 已领取数量。
     */
    private Integer receivedCount;

    /**
     * 已使用数量。
     */
    private Integer usedCount;

    /**
     * 有效期开始时间。
     */
    private LocalDateTime validStartTime;

    /**
     * 有效期结束时间。
     */
    private LocalDateTime validEndTime;

    /**
     * 状态，0 禁用，1 启用。
     */
    private Integer status;

    /**
     * 使用说明。
     */
    private String description;

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
