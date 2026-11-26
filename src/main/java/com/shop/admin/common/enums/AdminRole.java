package com.shop.admin.common.enums;

import lombok.Getter;

/**
 * 管理员角色枚举。
 *
 * @author shop-admin
 */
@Getter
public enum AdminRole {

    /**
     * 超级管理员，固定拥有全部菜单与接口权限。
     */
    SUPER_ADMIN("SUPER_ADMIN", "超级管理员"),

    /**
     * 普通管理员，默认负责商品、图库和统计业务，菜单可由超级管理员动态分配。
     */
    ADMIN("ADMIN", "管理员");

    private final String code;

    private final String description;

    AdminRole(String code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 根据角色编码获取枚举。
     *
     * @param code 角色编码
     * @return 角色枚举
     */
    public static AdminRole fromCode(String code) {
        for (AdminRole role : values()) {
            if (role.getCode().equals(code)) {
                return role;
            }
        }
        throw new IllegalArgumentException("未知的管理员角色: " + code);
    }

    /**
     * 判断角色编码是否合法。
     *
     * @param code 角色编码
     * @return 是否合法
     */
    public static boolean isValidCode(String code) {
        for (AdminRole role : values()) {
            if (role.getCode().equals(code)) {
                return true;
            }
        }
        return false;
    }
}
