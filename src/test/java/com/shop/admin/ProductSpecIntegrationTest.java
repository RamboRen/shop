package com.shop.admin;

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
 * 商品规格管理集成测试。
 *
 * @author shop-admin
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ProductSpecIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateQueryUpdateAndDeleteSpec() throws Exception {
        String token = login("admin");
        String specName = "测试规格" + UUID.randomUUID().toString().substring(0, 8);
        Map<String, Object> createBody = Map.of(
                "name", specName,
                "values", List.of("黑色", "白色"),
                "status", 1,
                "sort", 1,
                "remark", "集成测试规格");

        MvcResult createResult = mockMvc.perform(post("/api/admin/product-specs")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andReturn();
        long specId = objectMapper.readTree(createResult.getResponse().getContentAsString())
                .path("data").asLong();

        mockMvc.perform(get("/api/admin/product-specs/" + specId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value(specName))
                .andExpect(jsonPath("$.data.values.length()").value(2))
                .andExpect(jsonPath("$.data.statusName").value("启用"));

        mockMvc.perform(get("/api/admin/product-specs")
                        .header("Authorization", "Bearer " + token)
                        .param("keyword", specName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.records[0].id").value(specId));

        Map<String, Object> updateBody = Map.of(
                "name", specName,
                "values", List.of("红色", "蓝色"),
                "status", 1,
                "sort", 2,
                "remark", "修改后的规格");
        mockMvc.perform(put("/api/admin/product-specs/" + specId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateBody)))
                .andExpect(status().isOk());

        mockMvc.perform(put("/api/admin/product-specs/" + specId + "/status")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":0}"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/admin/product-specs/" + specId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.values[0]").value("红色"))
                .andExpect(jsonPath("$.data.status").value(0));

        mockMvc.perform(delete("/api/admin/product-specs/" + specId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/admin/product-specs/" + specId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldRejectInvalidSpecRequest() throws Exception {
        String token = login("admin");
        mockMvc.perform(post("/api/admin/product-specs")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\",\"values\":[]}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));

        String specName = "重复规格" + UUID.randomUUID().toString().substring(0, 8);
        Map<String, Object> createBody = Map.of(
                "name", specName,
                "values", List.of("黑色", "白色"));
        MvcResult createResult = mockMvc.perform(post("/api/admin/product-specs")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createBody)))
                .andExpect(status().isOk())
                .andReturn();
        long specId = objectMapper.readTree(createResult.getResponse().getContentAsString())
                .path("data").asLong();
        try {
            mockMvc.perform(post("/api/admin/product-specs")
                            .header("Authorization", "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createBody)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.code").value(409));

            Map<String, Object> duplicateValuesBody = Map.of(
                    "name", specName + "二",
                    "values", List.of("黑色", "黑色"));
            mockMvc.perform(post("/api/admin/product-specs")
                            .header("Authorization", "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(duplicateValuesBody)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400));
        } finally {
            mockMvc.perform(delete("/api/admin/product-specs/" + specId)
                            .header("Authorization", "Bearer " + token));
        }
    }

    @Test
    void normalAdminShouldAccessSpecs() throws Exception {
        String token = login("operator");
        mockMvc.perform(get("/api/admin/product-specs")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").isNumber());
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
}
