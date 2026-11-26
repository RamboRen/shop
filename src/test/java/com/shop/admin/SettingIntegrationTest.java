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

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 系统设置集成测试。
 *
 * @author shop-admin
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SettingIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldPageQueryGroupsAndConfigMap() throws Exception {
        String token = login("admin");

        mockMvc.perform(get("/api/admin/settings")
                        .header("Authorization", "Bearer " + token)
                        .param("configGroup", "BASIC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(4))
                .andExpect(jsonPath("$.data.records[0].configKey").value("site.name"))
                .andExpect(jsonPath("$.data.records[0].configGroupName").value("基础设置"))
                .andExpect(jsonPath("$.data.records[0].valueTypeName").value("文本"));

        mockMvc.perform(get("/api/admin/settings")
                        .header("Authorization", "Bearer " + token)
                        .param("keyword", "site.icp"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.records[0].configKey").value("site.icp"));

        mockMvc.perform(get("/api/admin/settings/groups")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(4))
                .andExpect(jsonPath("$.data[0].groupCode").value("BASIC"))
                .andExpect(jsonPath("$.data[0].items.length()").value(4))
                .andExpect(jsonPath("$.data[3].groupCode").value("PAYMENT"));

        mockMvc.perform(get("/api/admin/settings/map")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data['site.name']").value("云商城"))
                .andExpect(jsonPath("$.data['trade.freight']").value("10.00"));
    }

    @Test
    void shouldCreateUpdateAndDeleteSetting() throws Exception {
        String token = login("admin");
        String configKey = "demo." + UUID.randomUUID().toString().replace("-", "").substring(0, 8);

        MvcResult createResult = mockMvc.perform(post("/api/admin/settings")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "configKey", configKey,
                                "configName", "自动化测试配置",
                                "configGroup", "BASIC",
                                "configValue", "初始值",
                                "valueType", "STRING",
                                "sort", 9))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andReturn();
        long id = objectMapper.readTree(createResult.getResponse().getContentAsString())
                .path("data").asLong();

        mockMvc.perform(get("/api/admin/settings/" + id)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.configKey").value(configKey))
                .andExpect(jsonPath("$.data.configValue").value("初始值"))
                .andExpect(jsonPath("$.data.configGroupName").value("基础设置"));

        mockMvc.perform(post("/api/admin/settings")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "configKey", configKey,
                                "configName", "重复配置",
                                "configGroup", "BASIC",
                                "valueType", "STRING"))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value(409));

        mockMvc.perform(put("/api/admin/settings/" + id)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "configName", "修改后的配置",
                                "configValue", "修改后的值",
                                "valueType", "TEXT",
                                "sort", 5))))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/admin/settings/" + id)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.configName").value("修改后的配置"))
                .andExpect(jsonPath("$.data.configValue").value("修改后的值"))
                .andExpect(jsonPath("$.data.valueTypeName").value("多行文本"))
                .andExpect(jsonPath("$.data.configKey").value(configKey));

        mockMvc.perform(delete("/api/admin/settings/" + id)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/admin/settings/" + id)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404));
    }

    @Test
    void shouldBatchSaveSettingsByGroup() throws Exception {
        String token = login("admin");

        mockMvc.perform(put("/api/admin/settings/batch")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "configGroup", "CONTACT",
                                "items", List.of(
                                        Map.of("configKey", "contact.phone", "configValue", "400-000-0000"),
                                        Map.of("configKey", "contact.email", "configValue", "new@example.com"))))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(2));

        mockMvc.perform(get("/api/admin/settings/map")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data['contact.phone']").value("400-000-0000"))
                .andExpect(jsonPath("$.data['contact.email']").value("new@example.com"));

        // 配置键不属于指定分组时拒绝保存
        mockMvc.perform(put("/api/admin/settings/batch")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "configGroup", "TRADE",
                                "items", List.of(
                                        Map.of("configKey", "site.name", "configValue", "越权修改"))))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));

        // 不存在的配置键返回 404
        mockMvc.perform(put("/api/admin/settings/batch")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "items", List.of(
                                        Map.of("configKey", "not.exist.key", "configValue", "x"))))))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404));
    }

    @Test
    void shouldRejectInvalidRequests() throws Exception {
        String token = login("admin");

        mockMvc.perform(get("/api/admin/settings")
                        .header("Authorization", "Bearer " + token)
                        .param("configGroup", "INVALID"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));

        mockMvc.perform(post("/api/admin/settings")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"configKey\":\"1bad-key\",\"configName\":\"测试\",\"configGroup\":\"BASIC\",\"valueType\":\"STRING\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));

        mockMvc.perform(put("/api/admin/settings/batch")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"configGroup\":\"BASIC\",\"items\":[]}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));

        mockMvc.perform(get("/api/admin/settings/999999")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404));
    }

    @Test
    void normalAdminShouldNotAccessSettings() throws Exception {
        String token = login("operator");
        mockMvc.perform(get("/api/admin/settings")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(403));
    }

    @Test
    void unauthenticatedShouldBeRejected() throws Exception {
        mockMvc.perform(get("/api/admin/settings"))
                .andExpect(status().isUnauthorized());
    }

    private String login(String username) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/admin/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                Map.of("username", username, "password", "admin123"))))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());
        return body.path("data").path("token").asText();
    }
}
