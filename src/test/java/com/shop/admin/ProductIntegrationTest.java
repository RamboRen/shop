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
 * 商品与分类管理集成测试。
 *
 * @author shop-admin
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ProductIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateQueryUpdateAndDeleteProduct() throws Exception {
        String token = obtainAdminToken();
        String productCode = "SPTEST" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Map<String, Object> createBody = Map.ofEntries(
                Map.entry("productCode", productCode),
                Map.entry("categoryId", 1),
                Map.entry("name", "集成测试商品"),
                Map.entry("subtitle", "测试副标题"),
                Map.entry("mainImage", "/uploads/demo/product-1.jpg"),
                Map.entry("imageUrls", List.of("/uploads/demo/product-1.jpg")),
                Map.entry("price", 199.00),
                Map.entry("originalPrice", 299.00),
                Map.entry("stock", 10),
                Map.entry("status", 0),
                Map.entry("sort", 1),
                Map.entry("detail", "集成测试详情"));

        MvcResult createResult = mockMvc.perform(post("/api/admin/products")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isNumber())
                .andReturn();
        long productId = objectMapper.readTree(createResult.getResponse().getContentAsString())
                .path("data").asLong();

        mockMvc.perform(get("/api/admin/products/" + productId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.productCode").value(productCode))
                .andExpect(jsonPath("$.data.categoryName").value("数码家电"))
                .andExpect(jsonPath("$.data.stock").value(10));

        mockMvc.perform(get("/api/admin/products")
                        .header("Authorization", "Bearer " + token)
                        .param("keyword", productCode))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.records[0].id").value(productId));

        Map<String, Object> updateBody = Map.of(
                "categoryId", 1,
                "name", "集成测试商品-已修改",
                "mainImage", "/uploads/demo/product-1.jpg",
                "imageUrls", List.of("/uploads/demo/product-1.jpg"),
                "price", 189.00,
                "originalPrice", 299.00,
                "stock", 20,
                "sort", 2);
        mockMvc.perform(put("/api/admin/products/" + productId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        mockMvc.perform(put("/api/admin/products/" + productId + "/status")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":1}"))
                .andExpect(status().isOk());

        mockMvc.perform(put("/api/admin/products/batch-status")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                Map.of("ids", List.of(productId), "status", 0))))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/api/admin/products/" + productId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/admin/products/" + productId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldRejectInvalidProductRequest() throws Exception {
        String token = obtainAdminToken();
        mockMvc.perform(post("/api/admin/products")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"categoryId\":1,\"name\":\"\",\"mainImage\":\"\","
                                + "\"price\":0,\"stock\":-1}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));
    }

    @Test
    void shouldRejectCategoryDeletionWhenProductsExist() throws Exception {
        String token = obtainAdminToken();
        mockMvc.perform(delete("/api/admin/product-categories/1")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));
    }

    private String obtainAdminToken() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/admin/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                Map.of("username", "admin", "password", "admin123"))))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode response = objectMapper.readTree(result.getResponse().getContentAsString());
        return response.path("data").path("token").asText();
    }
}
