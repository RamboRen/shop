package com.shop.admin.menu.controller;

import com.shop.admin.common.api.ApiResult;
import com.shop.admin.menu.dto.MenuCreateRequest;
import com.shop.admin.menu.dto.MenuUpdateRequest;
import com.shop.admin.menu.dto.RoleMenuAssignRequest;
import com.shop.admin.menu.service.MenuService;
import com.shop.admin.menu.vo.MenuVO;
import com.shop.admin.menu.vo.RoleMenuAssignmentVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 菜单与角色菜单权限管理接口。
 *
 * @author shop-admin
 */
@RestController
@RequestMapping("/api/admin/menus")
@RequiredArgsConstructor
@PreAuthorize("hasRole('SUPER_ADMIN') or hasAuthority('system:menu')")
@Tag(name = "菜单权限", description = "菜单维护、角色菜单分配与动态菜单权限")
@SecurityRequirement(name = "bearerAuth")
public class MenuController {

    private final MenuService menuService;

    /**
     * 查询全部菜单平铺列表。
     *
     * @return 菜单列表
     */
    @GetMapping
    @Operation(summary = "查询全部菜单", description = "返回包含隐藏、禁用菜单的平铺列表，供菜单管理页使用")
    public ApiResult<List<MenuVO>> listMenus() {
        return ApiResult.success(menuService.listAllMenus());
    }

    /**
     * 查询全部菜单树。
     *
     * @return 菜单树
     */
    @GetMapping("/tree")
    @Operation(summary = "查询全部菜单树", description = "返回包含隐藏、禁用菜单的树形结构")
    public ApiResult<List<MenuVO>> getMenuTree() {
        return ApiResult.success(menuService.getAllMenuTree());
    }

    /**
     * 新增菜单。
     *
     * @param request 新增请求
     * @return 新菜单编号
     */
    @PostMapping
    @Operation(summary = "新增菜单")
    public ApiResult<Long> createMenu(@Valid @RequestBody MenuCreateRequest request) {
        return ApiResult.success(menuService.createMenu(request));
    }

    /**
     * 修改菜单。
     *
     * @param id      菜单编号
     * @param request 修改请求
     * @return 操作结果
     */
    @PutMapping("/{id}")
    @Operation(summary = "修改菜单")
    public ApiResult<Void> updateMenu(@PathVariable Long id,
                                      @Valid @RequestBody MenuUpdateRequest request) {
        menuService.updateMenu(id, request);
        return ApiResult.success();
    }

    /**
     * 删除菜单。
     *
     * @param id 菜单编号
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除菜单", description = "存在子菜单时不允许删除")
    public ApiResult<Void> deleteMenu(@PathVariable Long id) {
        menuService.deleteMenu(id);
        return ApiResult.success();
    }

    /**
     * 查询角色已分配的菜单。
     *
     * @param roleCode 角色编码
     * @return 角色菜单分配信息
     */
    @GetMapping("/roles/{roleCode}")
    @Operation(summary = "查询角色菜单")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ApiResult<RoleMenuAssignmentVO> getRoleMenus(@PathVariable String roleCode) {
        return ApiResult.success(menuService.getRoleMenuAssignment(roleCode));
    }

    /**
     * 重新分配角色菜单。
     *
     * @param roleCode 角色编码
     * @param request  分配请求
     * @return 操作结果
     */
    @PutMapping("/roles/{roleCode}")
    @Operation(summary = "分配角色菜单", description = "仅超级管理员可操作，保存后立即影响该角色可见菜单与接口权限")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ApiResult<Void> assignRoleMenus(@PathVariable String roleCode,
                                           @Valid @RequestBody RoleMenuAssignRequest request) {
        menuService.assignRoleMenus(roleCode, request);
        return ApiResult.success();
    }
}
