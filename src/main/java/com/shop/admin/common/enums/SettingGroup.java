package com.shop.admin.common.enums;

import lombok.Getter;

/**
 * 系统设置分组枚举。
 *
 * @author shop-admin
 */
@Getter
public enum SettingGroup {

    /**
     * 基础设置：站点名称、Logo、备案号等。
     */
    BASIC("BASIC", "基础设置"),

    /**
     * 联系方式：客服电话、邮箱、地址等。
     */
    CONTACT("CONTACT", "联系方式"),

    /**
     * 交易设置：运费、包邮门槛、订单自动取消时长等。
     */
    TRADE("TRADE", "交易设置"),

    /**
     * 支付设置：支持的支付方式、收款说明等。
     */
    PAYMENT("PAYMENT", "支付设置");

    private final String code;

    private final String description;

    SettingGroup(String code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 根据分组编码获取枚举。
     *
     * @param code 分组编码
     * @return 分组枚举
     */
    public static SettingGroup fromCode(String code) {
        for (SettingGroup group : values()) {
            if (group.getCode().equalsIgnoreCase(code)) {
                return group;
            }
        }
        throw new IllegalArgumentException("未知的设置分组: " + code);
    }
}
