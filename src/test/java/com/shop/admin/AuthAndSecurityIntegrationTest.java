package com.shop.admin;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Map;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 登录、鉴权与跨域集成测试。
 *
 * @author shop-admin
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthAndSecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void loginShouldReturnTokenAndAdminProfile() throws Exception {
        mockMvc.perform(post("/api/admin/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                Map.of("username", "admin", "password", "admin123"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.token").isNotEmpty())
                .andExpect(jsonPath("$.data.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.data.expiresIn").value(7200))
                .andExpect(jsonPath("$.data.admin.username").value("admin"))
                .andExpect(jsonPath("$.data.admin.roleCode").value("SUPER_ADMIN"))
                .andExpect(jsonPath("$.data.admin.password").doesNotExist())
                .andExpect(jsonPath("$.data.menus.length()").value(6))
                .andExpect(jsonPath("$.data.permissions", hasItem("system:menu")))
                .andExpect(jsonPath("$.data.permissions", hasItem("order:view")))
                .andExpect(jsonPath("$.data.permissions", hasItem("user:view")))
                .andExpect(jsonPath("$.data.permissions", hasItem("system:setting")));
    }

    @Test
    void loginWithWrongPasswordShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(post("/api/admin/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                Map.of("username", "admin", "password", "wrong-password"))))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(401));
    }

    @Test
    void requestWithoutTokenShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/api/admin/stats/overview"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(401))
                .andExpect(jsonPath("$.message").value("未登录或登录已过期"));
    }

    @Test
    void normalAdminShouldNotManageAdmins() throws Exception {
        String token = obtainToken("operator", "admin123");
        mockMvc.perform(get("/api/admin/admins").header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(403));
    }

    @Test
    void normalAdminShouldAccessStats() throws Exception {
        String token = obtainToken("operator", "admin123");
        mockMvc.perform(get("/api/admin/stats/overview")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.productCount").isNumber());
    }

    @Test
    void corsPreflightShouldBeAllowed() throws Exception {
        mockMvc.perform(options("/api/admin/products")
                        .header("Origin", "http://localhost:5173")
                        .header("Access-Control-Request-Method", "GET")
                        .header("Access-Control-Request-Headers", "Authorization"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:5173"));
    }

    @Test
    void openApiDocumentShouldBePubliclyAvailable() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.info.title").value("商城后台管理系统 API"))
                .andExpect(jsonPath("$.info.version").value("1.4.0"))
                .andExpect(jsonPath("$.components.securitySchemes.bearerAuth.scheme").value("bearer"))
                .andExpect(jsonPath("$.paths['/api/admin/auth/login'].post").exists())
                .andExpect(jsonPath("$.paths['/api/admin/product-specs'].get").exists())
                .andExpect(jsonPath("$.paths['/api/admin/coupons'].post").exists())
                .andExpect(jsonPath("$.paths['/api/admin/orders'].get").exists())
                .andExpect(jsonPath("$.paths['/api/admin/orders/{id}/status'].put").exists());
    }

    private String obtainToken(String username, String password) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/admin/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                Map.of("username", username, "password", password))))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode response = objectMapper.readTree(result.getResponse().getContentAsString());
        return response.path("data").path("token").asText();
    }
}
