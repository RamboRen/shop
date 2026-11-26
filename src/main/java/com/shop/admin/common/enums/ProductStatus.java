package com.shop.admin.common.enums;

import lombok.Getter;

/**
 * 商品上下架状态枚举。
 *
 * @author shop-admin
 */
@Getter
public enum ProductStatus {

    /**
     * 下架状态。
     */
    OFF_SHELF(0, "下架"),

    /**
     * 上架状态。
     */
    ON_SHELF(1, "上架");

    private final int code;

    private final String description;

    ProductStatus(int code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 根据状态码获取枚举。
     *
     * @param code 状态码
     * @return 商品状态枚举
     */
    public static ProductStatus fromCode(int code) {
        for (ProductStatus status : values()) {
            if (status.getCode() == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("未知的商品状态: " + code);
    }
}
