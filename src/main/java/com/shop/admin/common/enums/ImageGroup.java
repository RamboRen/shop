package com.shop.admin.common.enums;

import lombok.Getter;

/**
 * 图库分组枚举。
 *
 * @author shop-admin
 */
@Getter
public enum ImageGroup {

    /**
     * 商品图片。
     */
    PRODUCT("PRODUCT", "商品图"),

    /**
     * 轮播图。
     */
    BANNER("BANNER", "轮播图"),

    /**
     * 头像。
     */
    AVATAR("AVATAR", "头像"),

    /**
     * 其他图片。
     */
    OTHER("OTHER", "其他");

    private final String code;

    private final String description;

    ImageGroup(String code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 根据分组编码获取枚举。
     *
     * @param code 分组编码
     * @return 图库分组
     */
    public static ImageGroup fromCode(String code) {
        for (ImageGroup group : values()) {
            if (group.getCode().equals(code)) {
                return group;
            }
        }
        throw new IllegalArgumentException("未知的图库分组: " + code);
    }
}
