package com.shop.admin.common.enums;

import lombok.Getter;

/**
 * 优惠券类型枚举。
 *
 * @author shop-admin
 */
@Getter
public enum CouponType {

    /**
     * 满减券，折扣值为减免金额。
     */
    AMOUNT("AMOUNT", "满减券"),

    /**
     * 折扣券，折扣值为折扣，例如 8.5 表示 8.5 折。
     */
    DISCOUNT("DISCOUNT", "折扣券");

    private final String code;

    private final String description;

    CouponType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 根据类型编码获取枚举。
     *
     * @param code 类型编码
     * @return 优惠券类型
     */
    public static CouponType fromCode(String code) {
        for (CouponType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("未知的优惠券类型: " + code);
    }
}
