package com.shop.admin.menu.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.shop.admin.common.enums.AdminRole;
import com.shop.admin.common.enums.CommonStatus;
import com.shop.admin.common.enums.MenuType;
import com.shop.admin.common.exception.BusinessException;
import com.shop.admin.menu.dto.MenuCreateRequest;
import com.shop.admin.menu.dto.MenuUpdateRequest;
import com.shop.admin.menu.dto.RoleMenuAssignRequest;
import com.shop.admin.menu.entity.MenuDO;
import com.shop.admin.menu.entity.RoleMenuDO;
import com.shop.admin.menu.mapper.MenuMapper;
import com.shop.admin.menu.mapper.RoleMenuMapper;
import com.shop.admin.menu.service.MenuService;
import com.shop.admin.menu.vo.MenuAccessVO;
import com.shop.admin.menu.vo.MenuVO;
import com.shop.admin.menu.vo.RoleMenuAssignmentVO;
import com.shop.admin.security.AdminPrincipal;
import com.shop.admin.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 菜单与角色菜单权限服务实现。
 *
 * @author shop-admin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService {

    private static final long ROOT_PARENT_ID = 0L;

    private static final int MAX_MENU_DEPTH = 20;

    private final MenuMapper menuMapper;

    private final RoleMenuMapper roleMenuMapper;

    @Override
    public MenuAccessVO getCurrentMenuAccess() {
        AdminPrincipal principal = SecurityUtils.getCurrentPrincipal();
        return getMenuAccessByRole(principal.getRoleCode());
    }

    @Override
    public MenuAccessVO getMenuAccessByRole(String roleCode) {
        AdminRole role = parseRole(roleCode);
        List<MenuDO> enabledMenus = loadEnabledMenus(role);
        List<String> permissions = extractPermissionCodes(enabledMenus);
        List<MenuDO> visibleMenus = enabledMenus.stream()
                .filter(menu -> Objects.equals(CommonStatus.ENABLED.getCode(), menu.getVisible()))
                .toList();
        return new MenuAccessVO(buildTree(visibleMenus), permissions);
    }

    @Override
    public List<String> getPermissionCodesByRole(String roleCode) {
        return extractPermissionCodes(loadEnabledMenus(parseRole(roleCode)));
    }

    @Override
    public List<MenuVO> listAllMenus() {
        return queryAllMenus().stream()
                .map(MenuVO::from)
                .toList();
    }

    @Override
    public List<MenuVO> getAllMenuTree() {
        return buildTree(queryAllMenus());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createMenu(MenuCreateRequest request) {
        MenuType menuType = parseMenuType(request.getMenuType());
        String routePath = normalizeRoutePath(request.getRoutePath());
        String permissionCode = normalizePermissionCode(menuType, request.getPermissionCode());
        Long parentId = request.getParentId() == null ? ROOT_PARENT_ID : request.getParentId();
        validateParent(parentId, null);
        validateRoutePathUnique(routePath, null);
        validatePermissionCodeUnique(permissionCode, null);

        MenuDO menuDO = new MenuDO();
        fillMenu(menuDO, parentId, request.getMenuName(), menuType, routePath, request.getComponent(),
                request.getIcon(), permissionCode, request.getSort(), request.getVisible(), request.getStatus());
        if (menuMapper.insert(menuDO) != 1) {
            throw new BusinessException(500, "新增菜单失败");
        }
        ensureSuperAdminMenuMapping(menuDO.getId());
        log.info("新增菜单成功，菜单编号: {}, 操作人: {}",
                menuDO.getId(), SecurityUtils.getCurrentAdminId());
        return menuDO.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateMenu(Long id, MenuUpdateRequest request) {
        getRequiredMenu(id, "菜单不存在");
        MenuType menuType = parseMenuType(request.getMenuType());
        String routePath = normalizeRoutePath(request.getRoutePath());
        String permissionCode = normalizePermissionCode(menuType, request.getPermissionCode());
        Long parentId = request.getParentId() == null ? ROOT_PARENT_ID : request.getParentId();
        validateParent(parentId, id);
        validateRoutePathUnique(routePath, id);
        validatePermissionCodeUnique(permissionCode, id);
        if (menuType == MenuType.MENU && countChildren(id) > 0) {
            throw new BusinessException(400, "存在子菜单的目录不能改为菜单类型");
        }
        String menuName = request.getMenuName().trim();
        if (menuName.isEmpty()) {
            throw new BusinessException(400, "菜单名称不能为空");
        }

        LambdaUpdateWrapper<MenuDO> wrapper = Wrappers.<MenuDO>lambdaUpdate()
                .eq(MenuDO::getId, id)
                .set(MenuDO::getParentId, parentId)
                .set(MenuDO::getMenuName, menuName)
                .set(MenuDO::getMenuType, menuType.getCode())
                .set(MenuDO::getRoutePath, routePath)
                .set(MenuDO::getComponent, trimToNull(request.getComponent()))
                .set(MenuDO::getIcon, trimToNull(request.getIcon()))
                .set(MenuDO::getPermissionCode, permissionCode)
                .set(MenuDO::getSort, request.getSort())
                .set(MenuDO::getVisible, request.getVisible())
                .set(MenuDO::getStatus, request.getStatus())
                .set(MenuDO::getUpdateTime, LocalDateTime.now());
        if (menuMapper.update(null, wrapper) != 1) {
            throw new BusinessException(500, "修改菜单失败");
        }
        ensureSuperAdminMenuMapping(id);
        log.info("修改菜单成功，菜单编号: {}, 操作人: {}", id, SecurityUtils.getCurrentAdminId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMenu(Long id) {
        getRequiredMenu(id, "菜单不存在");
        if (countChildren(id) > 0) {
            throw new BusinessException(400, "存在子菜单，不能删除");
        }
        roleMenuMapper.delete(Wrappers.<RoleMenuDO>lambdaQuery()
                .eq(RoleMenuDO::getMenuId, id));
        if (menuMapper.deleteById(id) != 1) {
            throw new BusinessException(500, "删除菜单失败");
        }
        log.info("删除菜单成功，菜单编号: {}, 操作人: {}", id, SecurityUtils.getCurrentAdminId());
    }

    @Override
    public RoleMenuAssignmentVO getRoleMenuAssignment(String roleCode) {
        AdminRole role = parseRole(roleCode);
        List<MenuDO> allMenus = queryAllMenus();
        Set<Long> assignedIds = queryAssignedMenuIds(role);
        List<Long> menuIds = allMenus.stream()
                .filter(menu -> role == AdminRole.SUPER_ADMIN || assignedIds.contains(menu.getId()))
                .map(MenuDO::getId)
                .toList();
        return new RoleMenuAssignmentVO(role.getCode(), role.getDescription(), menuIds);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignRoleMenus(String roleCode, RoleMenuAssignRequest request) {
        AdminPrincipal principal = SecurityUtils.getCurrentPrincipal();
        if (!AdminRole.SUPER_ADMIN.getCode().equals(principal.getRoleCode())) {
            throw new BusinessException(403, "仅超级管理员可以分配角色菜单");
        }
        AdminRole role = parseRole(roleCode);
        if (role == AdminRole.SUPER_ADMIN) {
            throw new BusinessException(400, "超级管理员默认拥有全部菜单，无需分配");
        }
        List<Long> requestMenuIds = request.getMenuIds();
        if (requestMenuIds == null) {
            throw new BusinessException(400, "菜单编号集合不能为空");
        }
        Map<Long, MenuDO> menuMap = new HashMap<>(Math.max(requestMenuIds.size(), 16));
        for (MenuDO menu : queryAllMenus()) {
            menuMap.put(menu.getId(), menu);
        }
        Set<Long> selectedIds = new LinkedHashSet<>(requestMenuIds);
        for (Long menuId : selectedIds) {
            if (!menuMap.containsKey(menuId)) {
                throw new BusinessException(400, "菜单不存在或已删除: " + menuId);
            }
        }
        Set<Long> expandedIds = expandAncestors(selectedIds, menuMap);

        roleMenuMapper.delete(Wrappers.<RoleMenuDO>lambdaQuery()
                .eq(RoleMenuDO::getRoleCode, role.getCode()));
        for (Long menuId : expandedIds) {
            RoleMenuDO roleMenuDO = new RoleMenuDO();
            roleMenuDO.setRoleCode(role.getCode());
            roleMenuDO.setMenuId(menuId);
            if (roleMenuMapper.insert(roleMenuDO) != 1) {
                throw new BusinessException(500, "保存角色菜单失败");
            }
        }
        log.info("分配角色菜单成功，角色: {}, 菜单数量: {}, 操作人: {}",
                role.getCode(), expandedIds.size(), principal.getAdminId());
    }

    private List<MenuDO> queryAllMenus() {
        return menuMapper.selectList(Wrappers.<MenuDO>lambdaQuery()
                .orderByAsc(MenuDO::getSort)
                .orderByAsc(MenuDO::getId));
    }

    private List<MenuDO> loadEnabledMenus(AdminRole role) {
        List<MenuDO> enabledMenus = menuMapper.selectList(Wrappers.<MenuDO>lambdaQuery()
                .eq(MenuDO::getStatus, CommonStatus.ENABLED.getCode())
                .orderByAsc(MenuDO::getSort)
                .orderByAsc(MenuDO::getId));
        if (role == AdminRole.SUPER_ADMIN) {
            return enabledMenus;
        }
        Set<Long> assignedIds = queryAssignedMenuIds(role);
        return enabledMenus.stream()
                .filter(menu -> assignedIds.contains(menu.getId()))
                .toList();
    }

    private Set<Long> queryAssignedMenuIds(AdminRole role) {
        return roleMenuMapper.selectList(Wrappers.<RoleMenuDO>lambdaQuery()
                        .eq(RoleMenuDO::getRoleCode, role.getCode()))
                .stream()
                .map(RoleMenuDO::getMenuId)
                .collect(Collectors.toSet());
    }

    private void ensureSuperAdminMenuMapping(Long menuId) {
        long count = roleMenuMapper.selectCount(Wrappers.<RoleMenuDO>lambdaQuery()
                .eq(RoleMenuDO::getRoleCode, AdminRole.SUPER_ADMIN.getCode())
                .eq(RoleMenuDO::getMenuId, menuId));
        if (count > 0) {
            return;
        }
        RoleMenuDO roleMenuDO = new RoleMenuDO();
        roleMenuDO.setRoleCode(AdminRole.SUPER_ADMIN.getCode());
        roleMenuDO.setMenuId(menuId);
        if (roleMenuMapper.insert(roleMenuDO) != 1) {
            throw new BusinessException(500, "保存超级管理员菜单权限失败");
        }
    }

    private List<String> extractPermissionCodes(List<MenuDO> menus) {
        return menus.stream()
                .map(MenuDO::getPermissionCode)
                .filter(StringUtils::hasText)
                .distinct()
                .sorted()
                .toList();
    }

    private List<MenuVO> buildTree(List<MenuDO> menus) {
        Map<Long, MenuVO> menuMap = new LinkedHashMap<>(Math.max(menus.size(), 16));
        for (MenuDO menu : menus) {
            MenuVO vo = MenuVO.from(menu);
            vo.setChildren(new ArrayList<>());
            menuMap.put(menu.getId(), vo);
        }
        List<MenuVO> roots = new ArrayList<>();
        for (MenuDO menu : menus) {
            MenuVO vo = menuMap.get(menu.getId());
            Long parentId = menu.getParentId();
            if (parentId == null || parentId == ROOT_PARENT_ID) {
                roots.add(vo);
                continue;
            }
            MenuVO parent = menuMap.get(parentId);
            if (parent != null) {
                parent.getChildren().add(vo);
            }
        }
        return roots;
    }

    private Set<Long> expandAncestors(Set<Long> selectedIds, Map<Long, MenuDO> menuMap) {
        Set<Long> expandedIds = new LinkedHashSet<>(selectedIds);
        for (Long menuId : selectedIds) {
            MenuDO current = menuMap.get(menuId);
            int depth = 0;
            while (current != null && current.getParentId() != null
                    && current.getParentId() != ROOT_PARENT_ID) {
                MenuDO parent = menuMap.get(current.getParentId());
                if (parent == null) {
                    throw new BusinessException(400, "父菜单不存在，无法完成分配");
                }
                expandedIds.add(parent.getId());
                current = parent;
                depth++;
                if (depth > MAX_MENU_DEPTH) {
                    throw new BusinessException(400, "菜单层级超过 " + MAX_MENU_DEPTH + " 层");
                }
            }
        }
        return expandedIds;
    }

    private void validateParent(Long parentId, Long menuId) {
        if (parentId == null || parentId == ROOT_PARENT_ID) {
            return;
        }
        MenuDO parent = getRequiredMenu(parentId, "父菜单不存在");
        if (!MenuType.CATALOG.getCode().equals(parent.getMenuType())) {
            throw new BusinessException(400, "父级菜单必须是目录类型");
        }
        if (menuId == null) {
            return;
        }
        MenuDO current = parent;
        int depth = 0;
        while (current != null) {
            if (Objects.equals(current.getId(), menuId)) {
                throw new BusinessException(400, "不能选择自身或子菜单作为父级");
            }
            if (current.getParentId() == null || current.getParentId() == ROOT_PARENT_ID) {
                break;
            }
            depth++;
            if (depth > MAX_MENU_DEPTH) {
                throw new BusinessException(400, "菜单层级超过 " + MAX_MENU_DEPTH + " 层");
            }
            current = menuMapper.selectById(current.getParentId());
        }
    }

    private void validateRoutePathUnique(String routePath, Long excludeId) {
        long count = menuMapper.selectCount(Wrappers.<MenuDO>lambdaQuery()
                .eq(MenuDO::getRoutePath, routePath)
                .ne(excludeId != null, MenuDO::getId, excludeId));
        if (count > 0) {
            throw new BusinessException(409, "路由地址已存在");
        }
    }

    private void validatePermissionCodeUnique(String permissionCode, Long excludeId) {
        if (!StringUtils.hasText(permissionCode)) {
            return;
        }
        long count = menuMapper.selectCount(Wrappers.<MenuDO>lambdaQuery()
                .eq(MenuDO::getPermissionCode, permissionCode)
                .ne(excludeId != null, MenuDO::getId, excludeId));
        if (count > 0) {
            throw new BusinessException(409, "权限编码已存在");
        }
    }

    private long countChildren(Long menuId) {
        return menuMapper.selectCount(Wrappers.<MenuDO>lambdaQuery()
                .eq(MenuDO::getParentId, menuId));
    }

    private MenuDO getRequiredMenu(Long id, String message) {
        MenuDO menuDO = menuMapper.selectById(id);
        if (menuDO == null) {
            throw new BusinessException(404, message);
        }
        return menuDO;
    }

    private MenuType parseMenuType(String code) {
        if (!MenuType.isValidCode(code)) {
            throw new BusinessException(400, "未知的菜单类型: " + code);
        }
        return MenuType.fromCode(code);
    }

    private AdminRole parseRole(String roleCode) {
        if (!AdminRole.isValidCode(roleCode)) {
            throw new BusinessException(400, "未知的角色编码: " + roleCode);
        }
        return AdminRole.fromCode(roleCode);
    }

    private String normalizeRoutePath(String routePath) {
        if (!StringUtils.hasText(routePath)) {
            throw new BusinessException(400, "路由地址不能为空");
        }
        String normalized = routePath.trim();
        if (!normalized.startsWith("/")) {
            throw new BusinessException(400, "路由地址必须以 / 开头");
        }
        return normalized;
    }

    private String normalizePermissionCode(MenuType menuType, String permissionCode) {
        String normalized = trimToNull(permissionCode);
        if (menuType == MenuType.MENU && normalized == null) {
            throw new BusinessException(400, "菜单类型必须配置权限编码");
        }
        if (menuType == MenuType.CATALOG && normalized != null) {
            throw new BusinessException(400, "目录类型不能配置权限编码");
        }
        return normalized;
    }

    private String trimToNull(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return value.trim();
    }

    private void fillMenu(MenuDO menuDO, Long parentId, String menuName, MenuType menuType,
                          String routePath, String component, String icon, String permissionCode,
                          Integer sort, Integer visible, Integer status) {
        menuDO.setParentId(parentId);
        menuDO.setMenuName(menuName.trim());
        menuDO.setMenuType(menuType.getCode());
        menuDO.setRoutePath(routePath);
        menuDO.setComponent(trimToNull(component));
        menuDO.setIcon(trimToNull(icon));
        menuDO.setPermissionCode(permissionCode);
        menuDO.setSort(sort == null ? 0 : sort);
        menuDO.setVisible(visible == null ? CommonStatus.ENABLED.getCode() : visible);
        menuDO.setStatus(status == null ? CommonStatus.ENABLED.getCode() : status);
    }
}
