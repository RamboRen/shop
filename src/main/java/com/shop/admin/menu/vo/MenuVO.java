package com.shop.admin.menu.vo;

import com.shop.admin.common.enums.MenuType;
import com.shop.admin.menu.entity.MenuDO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 菜单视图对象。
 *
 * @author shop-admin
 */
@Getter
@Setter
@Schema(description = "菜单信息")
public class MenuVO {

    @Schema(description = "菜单编号", example = "1")
    private Long id;

    @Schema(description = "父菜单编号，0 表示顶级菜单", example = "0")
    private Long parentId;

    @Schema(description = "菜单名称", example = "数据概览")
    private String menuName;

    @Schema(description = "菜单类型：CATALOG 目录，MENU 菜单", example = "MENU")
    private String menuType;

    @Schema(description = "菜单类型名称", example = "菜单")
    private String menuTypeName;

    @Schema(description = "前端路由地址", example = "/dashboard")
    private String routePath;

    @Schema(description = "前端组件路径", example = "dashboard/index")
    private String component;

    @Schema(description = "菜单图标", example = "DataLine")
    private String icon;

    @Schema(description = "接口权限编码", example = "stats:view")
    private String permissionCode;

    @Schema(description = "排序值", example = "1")
    private Integer sort;

    @Schema(description = "是否显示：0 隐藏，1 显示", example = "1")
    private Integer visible;

    @Schema(description = "状态：0 禁用，1 启用", example = "1")
    private Integer status;

    @Schema(description = "子菜单")
    private List<MenuVO> children;

    /**
     * 根据菜单数据对象构建视图对象。
     *
     * @param menuDO 菜单数据对象
     * @return 菜单视图对象
     */
    public static MenuVO from(MenuDO menuDO) {
        MenuVO vo = new MenuVO();
        vo.setId(menuDO.getId());
        vo.setParentId(menuDO.getParentId());
        vo.setMenuName(menuDO.getMenuName());
        vo.setMenuType(menuDO.getMenuType());
        vo.setMenuTypeName(MenuType.fromCode(menuDO.getMenuType()).getDescription());
        vo.setRoutePath(menuDO.getRoutePath());
        vo.setComponent(menuDO.getComponent());
        vo.setIcon(menuDO.getIcon());
        vo.setPermissionCode(menuDO.getPermissionCode());
        vo.setSort(menuDO.getSort());
        vo.setVisible(menuDO.getVisible());
        vo.setStatus(menuDO.getStatus());
        return vo;
    }
}
