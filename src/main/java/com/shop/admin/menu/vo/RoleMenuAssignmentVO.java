package com.shop.admin.menu.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.List;

/**
 * 角色菜单分配视图对象。
 *
 * @author shop-admin
 */
@Getter
@Schema(description = "角色菜单分配信息")
public class RoleMenuAssignmentVO {

    @Schema(description = "角色编码", example = "ADMIN")
    private final String roleCode;

    @Schema(description = "角色名称", example = "管理员")
    private final String roleName;

    @Schema(description = "已分配的菜单编号集合")
    private final List<Long> menuIds;

    /**
     * 构造角色菜单分配视图对象。
     *
     * @param roleCode 角色编码
     * @param roleName 角色名称
     * @param menuIds  菜单编号集合
     */
    public RoleMenuAssignmentVO(String roleCode, String roleName, List<Long> menuIds) {
        this.roleCode = roleCode;
        this.roleName = roleName;
        this.menuIds = menuIds;
    }
}
