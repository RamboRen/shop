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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 订单管理集成测试。
 *
 * @author shop-admin
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class OrderIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldPageAndQueryOrderDetail() throws Exception {
        String token = login("admin");
        mockMvc.perform(get("/api/admin/orders")
                        .header("Authorization", "Bearer " + token)
                        .param("keyword", "SO20261109002")
                        .param("status", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.records[0].orderNo").value("SO20261109002"))
                .andExpect(jsonPath("$.data.records[0].statusName").value("待收货"))
                .andExpect(jsonPath("$.data.records[0].payTypeName").value("支付宝"))
                .andExpect(jsonPath("$.data.records[0].receiverName").value("李娜"));

        mockMvc.perform(get("/api/admin/orders/2")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.orderNo").value("SO20261109002"))
                .andExpect(jsonPath("$.data.receiverPhone").value("13800000002"))
                .andExpect(jsonPath("$.data.itemCount").value(1))
                .andExpect(jsonPath("$.data.items.length()").value(1))
                .andExpect(jsonPath("$.data.items[0].productName").value("智能运动手表"))
                .andExpect(jsonPath("$.data.items[0].totalAmount").value(599.00));
    }

    @Test
    void shouldTransitOrderStatusAndDelete() throws Exception {
        String token = login("admin");

        updateStatus(token, 5L, 1);
        mockMvc.perform(get("/api/admin/orders/5")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.statusName").value("待发货"))
                .andExpect(jsonPath("$.data.payTime").isNotEmpty());

        updateStatus(token, 5L, 2);
        mockMvc.perform(get("/api/admin/orders/5")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.statusName").value("待收货"))
                .andExpect(jsonPath("$.data.shipTime").isNotEmpty());

        updateStatus(token, 5L, 3);
        mockMvc.perform(get("/api/admin/orders/5")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.statusName").value("已完成"))
                .andExpect(jsonPath("$.data.finishTime").isNotEmpty());

        mockMvc.perform(put("/api/admin/orders/5/status")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":2}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));

        mockMvc.perform(delete("/api/admin/orders/5")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/admin/orders/5")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());

        updateStatus(token, 1L, 4);
        mockMvc.perform(get("/api/admin/orders/1")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.statusName").value("已取消"))
                .andExpect(jsonPath("$.data.cancelTime").isNotEmpty());
        mockMvc.perform(delete("/api/admin/orders/1")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void shouldRejectInvalidTransitionAndDeletion() throws Exception {
        String token = login("admin");

        mockMvc.perform(put("/api/admin/orders/3/status")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":1}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));

        mockMvc.perform(put("/api/admin/orders/3/status")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":9}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));

        mockMvc.perform(delete("/api/admin/orders/2")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));

        mockMvc.perform(get("/api/admin/orders")
                        .header("Authorization", "Bearer " + token)
                        .param("startDate", "2026-12-01")
                        .param("endDate", "2026-11-01"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));
    }

    @Test
    void normalAdminShouldAccessOrders() throws Exception {
        String token = login("operator");
        mockMvc.perform(get("/api/admin/orders")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").isNumber());
    }

    private void updateStatus(String token, Long orderId, int status) throws Exception {
        mockMvc.perform(put("/api/admin/orders/" + orderId + "/status")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("status", status))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
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
