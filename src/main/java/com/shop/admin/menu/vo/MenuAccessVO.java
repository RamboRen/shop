package com.shop.admin.menu.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.List;

/**
 * 当前管理员菜单权限视图对象。
 *
 * @author shop-admin
 */
@Getter
@Schema(description = "当前管理员菜单与接口权限")
public class MenuAccessVO {

    @Schema(description = "可显示的菜单树")
    private final List<MenuVO> menus;

    @Schema(description = "接口权限编码集合", example = "[\"stats:view\",\"product:view\"]")
    private final List<String> permissions;

    /**
     * 构造菜单权限视图对象。
     *
     * @param menus       菜单树
     * @param permissions 接口权限编码集合
     */
    public MenuAccessVO(List<MenuVO> menus, List<String> permissions) {
        this.menus = menus;
        this.permissions = permissions;
    }
}
