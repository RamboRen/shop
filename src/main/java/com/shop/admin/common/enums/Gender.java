package com.shop.admin.common.enums;

import lombok.Getter;

/**
 * 用户性别枚举。
 *
 * @author shop-admin
 */
@Getter
public enum Gender {

    /**
     * 未知。
     */
    UNKNOWN("UNKNOWN", "未知"),

    /**
     * 男。
     */
    MALE("MALE", "男"),

    /**
     * 女。
     */
    FEMALE("FEMALE", "女");

    private final String code;

    private final String description;

    Gender(String code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 根据编码获取枚举，非法编码返回未知。
     *
     * @param code 性别编码
     * @return 性别枚举
     */
    public static Gender fromCode(String code) {
        for (Gender gender : values()) {
            if (gender.getCode().equals(code)) {
                return gender;
            }
        }
        return UNKNOWN;
    }
}
