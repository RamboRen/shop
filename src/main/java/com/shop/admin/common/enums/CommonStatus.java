package com.shop.admin.common.enums;

import lombok.Getter;

/**
 * 通用启用状态枚举。
 *
 * @author shop-admin
 */
@Getter
public enum CommonStatus {

    /**
     * 禁用状态。
     */
    DISABLED(0, "禁用"),

    /**
     * 启用状态。
     */
    ENABLED(1, "启用");

    private final int code;

    private final String description;

    CommonStatus(int code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 根据状态码获取枚举。
     *
     * @param code 状态码
     * @return 状态枚举
     */
    public static CommonStatus fromCode(int code) {
        for (CommonStatus status : values()) {
            if (status.getCode() == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("未知的启用状态: " + code);
    }
}
