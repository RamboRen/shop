package com.shop.admin;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shop.admin.menu.entity.RoleMenuDO;
import com.shop.admin.menu.mapper.RoleMenuMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 动态菜单与角色菜单权限集成测试。
 *
 * @author shop-admin
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class MenuIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RoleMenuMapper roleMenuMapper;

    @Test
    void operatorMenuShouldBeFilteredByRole() throws Exception {
        String token = login("operator");
        mockMvc.perform(get("/api/admin/auth/menus")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.permissions", hasItem("stats:view")))
                .andExpect(jsonPath("$.data.permissions", hasItem("product:view")))
                .andExpect(jsonPath("$.data.permissions", hasItem("product:spec")))
                .andExpect(jsonPath("$.data.permissions", hasItem("product:coupon")))
                .andExpect(jsonPath("$.data.permissions", hasItem("order:view")))
                .andExpect(jsonPath("$.data.permissions", hasItem("user:view")))
                .andExpect(jsonPath("$.data.permissions", not(hasItem("system:admin"))))
                .andExpect(jsonPath("$.data.permissions", not(hasItem("system:setting"))))
                .andExpect(jsonPath("$.data.menus.length()").value(5))
                .andExpect(jsonPath("$.data.menus[0].menuName").value("数据概览"))
                .andExpect(jsonPath("$.data.menus[1].menuName").value("商品管理"))
                .andExpect(jsonPath("$.data.menus[1].children.length()").value(4))
                .andExpect(jsonPath("$.data.menus[1].children[2].menuName").value("规格管理"))
                .andExpect(jsonPath("$.data.menus[1].children[2].routePath").value("/product/spec"))
                .andExpect(jsonPath("$.data.menus[1].children[3].menuName").value("优惠券管理"))
                .andExpect(jsonPath("$.data.menus[1].children[3].routePath").value("/product/coupon"))
                .andExpect(jsonPath("$.data.menus[2].menuName").value("图库管理"))
                .andExpect(jsonPath("$.data.menus[3].menuName").value("订单管理"))
                .andExpect(jsonPath("$.data.menus[3].routePath").value("/order"))
                .andExpect(jsonPath("$.data.menus[4].menuName").value("用户管理"))
                .andExpect(jsonPath("$.data.menus[4].routePath").value("/user"));

        mockMvc.perform(get("/api/admin/menus")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(403));
    }

    @Test
    void assigningChildMenuShouldAutomaticallyIncludeParent() throws Exception {
        String token = login("admin");
        List<Long> original = getRoleMenuIds(token);
        try {
            assignRoleMenus(token, List.of(3L));
            List<Long> assigned = getRoleMenuIds(token);
            assertEquals(2, assigned.size());
            assertTrue(assigned.containsAll(List.of(2L, 3L)));
            mockMvc.perform(get("/api/admin/auth/menus")
                            .header("Authorization", "Bearer " + login("operator")))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.menus.length()").value(1))
                    .andExpect(jsonPath("$.data.menus[0].menuName").value("商品管理"))
                    .andExpect(jsonPath("$.data.menus[0].children.length()").value(1))
                    .andExpect(jsonPath("$.data.menus[0].children[0].menuName").value("商品列表"));
        } finally {
            assignRoleMenus(token, original);
        }
    }

    @Test
    void grantingMenuPermissionShouldAllowApiAccess() throws Exception {
        String adminToken = login("admin");
        String operatorToken = login("operator");
        List<Long> original = getRoleMenuIds(adminToken);
        try {
            assignRoleMenus(adminToken, List.of(6L, 7L));

            mockMvc.perform(get("/api/admin/auth/menus")
                            .header("Authorization", "Bearer " + operatorToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.permissions", hasItem("system:admin")))
                    .andExpect(jsonPath("$.data.permissions", not(hasItem("product:view"))))
                    .andExpect(jsonPath("$.data.menus.length()").value(1))
                    .andExpect(jsonPath("$.data.menus[0].menuName").value("系统管理"))
                    .andExpect(jsonPath("$.data.menus[0].children[0].menuName").value("管理员管理"));

            mockMvc.perform(get("/api/admin/admins")
                            .header("Authorization", "Bearer " + operatorToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.total").isNumber());
        } finally {
            assignRoleMenus(adminToken, original);
        }

        mockMvc.perform(get("/api/admin/admins")
                        .header("Authorization", "Bearer " + operatorToken))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(403));
    }

    @Test
    void menuManagerShouldNotAssignRoleMenus() throws Exception {
        String adminToken = login("admin");
        String operatorToken = login("operator");
        List<Long> original = getRoleMenuIds(adminToken);
        try {
            assignRoleMenus(adminToken, List.of(6L, 8L));

            mockMvc.perform(get("/api/admin/menus")
                            .header("Authorization", "Bearer " + operatorToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200));

            mockMvc.perform(put("/api/admin/menus/roles/ADMIN")
                            .header("Authorization", "Bearer " + operatorToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(Map.of("menuIds", List.of(1L)))))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.code").value(403));
        } finally {
            assignRoleMenus(adminToken, original);
        }
    }

    @Test
    void roleMenuChangeShouldImmediatelyAffectMenuAndApiPermission() throws Exception {
        String adminToken = login("admin");
        String operatorToken = login("operator");
        List<Long> original = getRoleMenuIds(adminToken);
        try {
            assignRoleMenus(adminToken, List.of(1L));

            mockMvc.perform(get("/api/admin/auth/menus")
                            .header("Authorization", "Bearer " + operatorToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.permissions.length()").value(1))
                    .andExpect(jsonPath("$.data.permissions[0]").value("stats:view"))
                    .andExpect(jsonPath("$.data.menus.length()").value(1))
                    .andExpect(jsonPath("$.data.menus[0].menuName").value("数据概览"));

            mockMvc.perform(get("/api/admin/products")
                            .header("Authorization", "Bearer " + operatorToken))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.code").value(403));
        } finally {
            assignRoleMenus(adminToken, original);
        }

        mockMvc.perform(get("/api/admin/products")
                        .header("Authorization", "Bearer " + operatorToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void superAdminShouldHaveAllMenusAndCannotBeReassigned() throws Exception {
        String token = login("admin");
        mockMvc.perform(get("/api/admin/auth/menus")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.menus.length()").value(6))
                .andExpect(jsonPath("$.data.permissions", hasItem("stats:view")))
                .andExpect(jsonPath("$.data.permissions", hasItem("system:admin")))
                .andExpect(jsonPath("$.data.permissions", hasItem("system:menu")))
                .andExpect(jsonPath("$.data.permissions", hasItem("product:spec")))
                .andExpect(jsonPath("$.data.permissions", hasItem("product:coupon")))
                .andExpect(jsonPath("$.data.permissions", hasItem("order:view")))
                .andExpect(jsonPath("$.data.permissions", hasItem("user:view")))
                .andExpect(jsonPath("$.data.permissions", hasItem("system:setting")));

        mockMvc.perform(get("/api/admin/menus/roles/SUPER_ADMIN")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.menuIds.length()").value(13));

        mockMvc.perform(put("/api/admin/menus/roles/SUPER_ADMIN")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("menuIds", List.of(1L)))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));
    }

    @Test
    void menuManagementShouldSupportCreateAndDelete() throws Exception {
        String token = login("admin");
        Map<String, Object> body = new HashMap<>(16);
        body.put("parentId", 6);
        body.put("menuName", "自动化测试菜单");
        body.put("menuType", "MENU");
        body.put("routePath", "/system/test-menu");
        body.put("component", "system/test-menu/index");
        body.put("icon", "Setting");
        body.put("permissionCode", "system:test-menu");
        body.put("sort", 99);
        body.put("visible", 1);
        body.put("status", 1);

        MvcResult createResult = mockMvc.perform(post("/api/admin/menus")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andReturn();
        Long menuId = objectMapper.readTree(createResult.getResponse().getContentAsString())
                .path("data").asLong();
        try {
            assertTrue(menuId > 0);
            assertEquals(1L, countSuperAdminMenuMapping(menuId));
            mockMvc.perform(get("/api/admin/menus/tree")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$..menuName", hasItem("自动化测试菜单")));

            Map<String, Object> childBody = new HashMap<>(16);
            childBody.put("parentId", menuId);
            childBody.put("menuName", "不允许的菜单子项");
            childBody.put("menuType", "MENU");
            childBody.put("routePath", "/system/test-menu/child");
            childBody.put("permissionCode", "system:test-child");
            childBody.put("sort", 100);
            childBody.put("visible", 1);
            childBody.put("status", 1);
            mockMvc.perform(post("/api/admin/menus")
                            .header("Authorization", "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(childBody)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400));
        } finally {
            mockMvc.perform(delete("/api/admin/menus/" + menuId)
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk());
        }

        mockMvc.perform(get("/api/admin/menus/tree")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$..menuName", not(hasItem("自动化测试菜单"))));
        assertEquals(0L, countSuperAdminMenuMapping(menuId));
    }

    private String login(String username) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/admin/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                Map.of("username", username, "password", "admin123"))))
                .andExpect(status().isOk())
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString())
                .path("data").path("token").asText();
    }

    private List<Long> getRoleMenuIds(String token) throws Exception {
        MvcResult result = mockMvc.perform(get("/api/admin/menus/roles/ADMIN")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode menuIds = objectMapper.readTree(result.getResponse().getContentAsString())
                .path("data").path("menuIds");
        List<Long> ids = new ArrayList<>();
        menuIds.forEach(node -> ids.add(node.asLong()));
        return ids;
    }

    private void assignRoleMenus(String token, List<Long> menuIds) throws Exception {
        mockMvc.perform(put("/api/admin/menus/roles/ADMIN")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("menuIds", menuIds))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    private long countSuperAdminMenuMapping(Long menuId) {
        return roleMenuMapper.selectCount(Wrappers.<RoleMenuDO>lambdaQuery()
                .eq(RoleMenuDO::getRoleCode, "SUPER_ADMIN")
                .eq(RoleMenuDO::getMenuId, menuId));
    }
}
