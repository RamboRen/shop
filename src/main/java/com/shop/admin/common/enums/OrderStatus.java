package com.shop.admin.common.enums;

import lombok.Getter;

/**
 * 订单状态枚举。
 *
 * @author shop-admin
 */
@Getter
public enum OrderStatus {

    /**
     * 待付款。
     */
    PENDING_PAYMENT(0, "待付款"),

    /**
     * 已付款，待发货。
     */
    PAID(1, "待发货"),

    /**
     * 已发货，待收货。
     */
    SHIPPED(2, "待收货"),

    /**
     * 已完成。
     */
    COMPLETED(3, "已完成"),

    /**
     * 已取消。
     */
    CANCELED(4, "已取消");

    private final int code;

    private final String description;

    OrderStatus(int code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 根据状态码获取枚举。
     *
     * @param code 状态码
     * @return 订单状态枚举
     */
    public static OrderStatus fromCode(int code) {
        for (OrderStatus status : values()) {
            if (status.getCode() == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("未知的订单状态: " + code);
    }

    /**
     * 判断订单状态是否允许流转到目标状态。
     *
     * @param target 目标状态
     * @return 是否允许流转
     */
    public boolean canTransitionTo(OrderStatus target) {
        if (target == null) {
            return false;
        }
        return switch (this) {
            case PENDING_PAYMENT -> target == PAID || target == CANCELED;
            case PAID -> target == SHIPPED || target == CANCELED;
            case SHIPPED -> target == COMPLETED;
            case COMPLETED, CANCELED -> false;
        };
    }
}
