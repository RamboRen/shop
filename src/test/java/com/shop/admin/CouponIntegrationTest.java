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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 优惠券管理集成测试。
 *
 * @author shop-admin
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CouponIntegrationTest {

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateQueryUpdateAndDeleteCoupon() throws Exception {
        String token = login("admin");
        String couponCode = "CPT" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String validStart = LocalDateTime.now().plusDays(1).withNano(0).format(DATE_TIME_FORMATTER);
        String validEnd = LocalDateTime.now().plusDays(30).withNano(0).format(DATE_TIME_FORMATTER);
        Map<String, Object> createBody = Map.ofEntries(
                Map.entry("couponCode", couponCode),
                Map.entry("name", "集成测试满减券"),
                Map.entry("couponType", "AMOUNT"),
                Map.entry("discountValue", 20.00),
                Map.entry("thresholdAmount", 100.00),
                Map.entry("totalCount", 100),
                Map.entry("validStartTime", validStart),
                Map.entry("validEndTime", validEnd),
                Map.entry("status", 1),
                Map.entry("description", "集成测试优惠券"));

        MvcResult createResult = mockMvc.perform(post("/api/admin/coupons")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andReturn();
        long couponId = objectMapper.readTree(createResult.getResponse().getContentAsString())
                .path("data").asLong();

        mockMvc.perform(get("/api/admin/coupons/" + couponId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.couponCode").value(couponCode))
                .andExpect(jsonPath("$.data.couponTypeName").value("满减券"))
                .andExpect(jsonPath("$.data.remainingCount").value(100))
                .andExpect(jsonPath("$.data.expired").value(false));

        mockMvc.perform(get("/api/admin/coupons")
                        .header("Authorization", "Bearer " + token)
                        .param("keyword", couponCode))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.records[0].id").value(couponId));

        Map<String, Object> updateBody = Map.ofEntries(
                Map.entry("name", "集成测试折扣券"),
                Map.entry("couponType", "DISCOUNT"),
                Map.entry("discountValue", 9.50),
                Map.entry("thresholdAmount", 0),
                Map.entry("totalCount", 200),
                Map.entry("validStartTime", validStart),
                Map.entry("validEndTime", validEnd),
                Map.entry("status", 1),
                Map.entry("description", "修改为折扣券"));
        mockMvc.perform(put("/api/admin/coupons/" + couponId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateBody)))
                .andExpect(status().isOk());

        mockMvc.perform(put("/api/admin/coupons/" + couponId + "/status")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":0}"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/admin/coupons/" + couponId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.couponType").value("DISCOUNT"))
                .andExpect(jsonPath("$.data.couponTypeName").value("折扣券"))
                .andExpect(jsonPath("$.data.remainingCount").value(200))
                .andExpect(jsonPath("$.data.status").value(0));

        mockMvc.perform(delete("/api/admin/coupons/" + couponId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/admin/coupons/" + couponId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldRejectInvalidCouponRules() throws Exception {
        String token = login("admin");
        String validStart = LocalDateTime.now().plusDays(1).withNano(0).format(DATE_TIME_FORMATTER);
        String validEnd = LocalDateTime.now().plusDays(30).withNano(0).format(DATE_TIME_FORMATTER);

        Map<String, Object> amountExceedsThreshold = Map.ofEntries(
                Map.entry("name", "非法满减券"),
                Map.entry("couponType", "AMOUNT"),
                Map.entry("discountValue", 200.00),
                Map.entry("thresholdAmount", 100.00),
                Map.entry("totalCount", 100),
                Map.entry("validStartTime", validStart),
                Map.entry("validEndTime", validEnd));
        mockMvc.perform(post("/api/admin/coupons")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(amountExceedsThreshold)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));

        Map<String, Object> invalidDiscount = Map.ofEntries(
                Map.entry("name", "非法折扣券"),
                Map.entry("couponType", "DISCOUNT"),
                Map.entry("discountValue", 10.00),
                Map.entry("thresholdAmount", 0),
                Map.entry("totalCount", 100),
                Map.entry("validStartTime", validStart),
                Map.entry("validEndTime", validEnd));
        mockMvc.perform(post("/api/admin/coupons")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDiscount)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));

        Map<String, Object> invalidPeriod = Map.ofEntries(
                Map.entry("name", "非法有效期券"),
                Map.entry("couponType", "AMOUNT"),
                Map.entry("discountValue", 20.00),
                Map.entry("thresholdAmount", 100.00),
                Map.entry("totalCount", 100),
                Map.entry("validStartTime", validEnd),
                Map.entry("validEndTime", validStart));
        mockMvc.perform(post("/api/admin/coupons")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidPeriod)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));

        Map<String, Object> totalBelowReceived = Map.ofEntries(
                Map.entry("name", "总量非法券"),
                Map.entry("couponType", "AMOUNT"),
                Map.entry("discountValue", 20.00),
                Map.entry("thresholdAmount", 100.00),
                Map.entry("totalCount", 10),
                Map.entry("validStartTime", validStart),
                Map.entry("validEndTime", validEnd),
                Map.entry("status", 1));
        mockMvc.perform(put("/api/admin/coupons/1")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(totalBelowReceived)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));
    }

    @Test
    void shouldRejectDuplicateCouponCode() throws Exception {
        String token = login("admin");
        String couponCode = "CPD" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Map<String, Object> createBody = Map.ofEntries(
                Map.entry("couponCode", couponCode),
                Map.entry("name", "编码重复测试券"),
                Map.entry("couponType", "AMOUNT"),
                Map.entry("discountValue", 20.00),
                Map.entry("thresholdAmount", 100.00),
                Map.entry("totalCount", 100),
                Map.entry("validStartTime", LocalDateTime.now().plusDays(1).withNano(0)
                        .format(DATE_TIME_FORMATTER)),
                Map.entry("validEndTime", LocalDateTime.now().plusDays(30).withNano(0)
                        .format(DATE_TIME_FORMATTER)));
        MvcResult createResult = mockMvc.perform(post("/api/admin/coupons")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createBody)))
                .andExpect(status().isOk())
                .andReturn();
        long couponId = objectMapper.readTree(createResult.getResponse().getContentAsString())
                .path("data").asLong();
        try {
            mockMvc.perform(post("/api/admin/coupons")
                            .header("Authorization", "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createBody)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.code").value(409));
        } finally {
            mockMvc.perform(delete("/api/admin/coupons/" + couponId)
                            .header("Authorization", "Bearer " + token));
        }
    }

    @Test
    void normalAdminShouldAccessCoupons() throws Exception {
        String token = login("operator");
        mockMvc.perform(get("/api/admin/coupons")
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
