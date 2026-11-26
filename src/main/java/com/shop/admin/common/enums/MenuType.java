package com.shop.admin.common.enums;

import lombok.Getter;

/**
 * 菜单类型枚举。
 *
 * @author shop-admin
 */
@Getter
public enum MenuType {

    /**
     * 目录，仅用于菜单分组，不对应前端页面权限。
     */
    CATALOG("CATALOG", "目录"),

    /**
     * 菜单，对应前端可访问页面。
     */
    MENU("MENU", "菜单");

    private final String code;

    private final String description;

    MenuType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 根据编码获取菜单类型。
     *
     * @param code 菜单类型编码
     * @return 菜单类型
     */
    public static MenuType fromCode(String code) {
        for (MenuType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("未知的菜单类型: " + code);
    }

    /**
     * 判断菜单类型编码是否合法。
     *
     * @param code 菜单类型编码
     * @return 是否合法
     */
    public static boolean isValidCode(String code) {
        for (MenuType type : values()) {
            if (type.getCode().equals(code)) {
                return true;
            }
        }
        return false;
    }
}
