package com.shop.admin.common.enums;

import lombok.Getter;

/**
 * 订单支付方式枚举。
 *
 * @author shop-admin
 */
@Getter
public enum OrderPayType {

    /**
     * 微信支付。
     */
    WECHAT("WECHAT", "微信支付"),

    /**
     * 支付宝。
     */
    ALIPAY("ALIPAY", "支付宝"),

    /**
     * 余额支付。
     */
    BALANCE("BALANCE", "余额支付");

    private final String code;

    private final String description;

    OrderPayType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 根据编码获取支付方式名称，编码为空时返回 null。
     *
     * @param code 支付方式编码
     * @return 支付方式名称
     */
    public static String describe(String code) {
        if (code == null || code.isBlank()) {
            return null;
        }
        for (OrderPayType payType : values()) {
            if (payType.getCode().equals(code)) {
                return payType.getDescription();
            }
        }
        return code;
    }
}
