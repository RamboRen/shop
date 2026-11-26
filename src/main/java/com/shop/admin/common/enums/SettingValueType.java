package com.shop.admin.common.enums;

import lombok.Getter;

/**
 * 系统设置值类型枚举，供前端渲染表单时判断输入控件类型。
 *
 * @author shop-admin
 */
@Getter
public enum SettingValueType {

    /**
     * 普通文本。
     */
    STRING("STRING", "文本"),

    /**
     * 多行文本。
     */
    TEXT("TEXT", "多行文本"),

    /**
     * 数字。
     */
    NUMBER("NUMBER", "数字"),

    /**
     * 布尔值，取值为 true/false。
     */
    BOOLEAN("BOOLEAN", "布尔");

    private final String code;

    private final String description;

    SettingValueType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 根据类型编码获取枚举。
     *
     * @param code 类型编码
     * @return 值类型枚举
     */
    public static SettingValueType fromCode(String code) {
        for (SettingValueType type : values()) {
            if (type.getCode().equalsIgnoreCase(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("未知的设置值类型: " + code);
    }
}
