package com.shop.admin.menu.service;

import com.shop.admin.menu.dto.MenuCreateRequest;
import com.shop.admin.menu.dto.MenuUpdateRequest;
import com.shop.admin.menu.dto.RoleMenuAssignRequest;
import com.shop.admin.menu.vo.MenuAccessVO;
import com.shop.admin.menu.vo.MenuVO;
import com.shop.admin.menu.vo.RoleMenuAssignmentVO;

import java.util.List;

/**
 * 菜单与角色菜单权限服务接口。
 *
 * @author shop-admin
 */
public interface MenuService {

    /**
     * 获取当前登录管理员的菜单树与接口权限。
     *
     * @return 菜单权限信息
     */
    MenuAccessVO getCurrentMenuAccess();

    /**
     * 根据角色获取菜单树与接口权限。
     *
     * @param roleCode 角色编码
     * @return 菜单权限信息
     */
    MenuAccessVO getMenuAccessByRole(String roleCode);

    /**
     * 根据角色获取启用菜单的接口权限编码。
     *
     * @param roleCode 角色编码
     * @return 权限编码集合
     */
    List<String> getPermissionCodesByRole(String roleCode);

    /**
     * 查询全部菜单平铺列表。
     *
     * @return 菜单列表
     */
    List<MenuVO> listAllMenus();

    /**
     * 查询全部菜单树，包含隐藏与禁用菜单。
     *
     * @return 菜单树
     */
    List<MenuVO> getAllMenuTree();

    /**
     * 新增菜单。
     *
     * @param request 新增请求
     * @return 新菜单编号
     */
    Long createMenu(MenuCreateRequest request);

    /**
     * 修改菜单。
     *
     * @param id      菜单编号
     * @param request 修改请求
     */
    void updateMenu(Long id, MenuUpdateRequest request);

    /**
     * 删除菜单。
     *
     * @param id 菜单编号
     */
    void deleteMenu(Long id);

    /**
     * 查询角色已分配的菜单。
     *
     * @param roleCode 角色编码
     * @return 角色菜单分配信息
     */
    RoleMenuAssignmentVO getRoleMenuAssignment(String roleCode);

    /**
     * 重新分配角色菜单。
     *
     * @param roleCode 角色编码
     * @param request  分配请求
     */
    void assignRoleMenus(String roleCode, RoleMenuAssignRequest request);
}
