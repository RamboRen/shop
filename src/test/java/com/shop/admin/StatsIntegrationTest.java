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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 后台统计接口集成测试。
 *
 * @author shop-admin
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class StatsIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldReturnOverviewStatistics() throws Exception {
        String token = obtainAdminToken();
        mockMvc.perform(get("/api/admin/stats/overview")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.totalSalesAmount").isNumber())
                .andExpect(jsonPath("$.data.todaySalesAmount").isNumber())
                .andExpect(jsonPath("$.data.totalOrderCount").isNumber())
                .andExpect(jsonPath("$.data.productCount").isNumber())
                .andExpect(jsonPath("$.data.galleryCount").isNumber())
                .andExpect(jsonPath("$.data.adminCount").isNumber());
    }

    @Test
    void shouldReturnSevenDaySalesTrend() throws Exception {
        String token = obtainAdminToken();
        mockMvc.perform(get("/api/admin/stats/sales-trend")
                        .header("Authorization", "Bearer " + token)
                        .param("days", "7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(7))
                .andExpect(jsonPath("$.data[0].saleDate").isNotEmpty())
                .andExpect(jsonPath("$.data[6].salesAmount").isNumber());
    }

    @Test
    void shouldReturnCategoryAndTopProductStatistics() throws Exception {
        String token = obtainAdminToken();
        mockMvc.perform(get("/api/admin/stats/category-distribution")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].productCount").isNumber());

        mockMvc.perform(get("/api/admin/stats/top-products")
                        .header("Authorization", "Bearer " + token)
                        .param("limit", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].salesVolume").isNumber());
    }

    @Test
    void shouldRejectInvalidTrendDays() throws Exception {
        String token = obtainAdminToken();
        mockMvc.perform(get("/api/admin/stats/sales-trend")
                        .header("Authorization", "Bearer " + token)
                        .param("days", "1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));
    }

    @Test
    void shouldReturnOrderStatistics() throws Exception {
        String token = obtainAdminToken();
        mockMvc.perform(get("/api/admin/stats/orders")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.totalOrderCount").isNumber())
                .andExpect(jsonPath("$.data.validOrderCount").isNumber())
                .andExpect(jsonPath("$.data.totalProductAmount").isNumber())
                .andExpect(jsonPath("$.data.totalPayAmount").isNumber())
                .andExpect(jsonPath("$.data.averageOrderAmount").isNumber())
                .andExpect(jsonPath("$.data.statusDistribution.length()").value(5))
                .andExpect(jsonPath("$.data.statusDistribution[0].status").value(0))
                .andExpect(jsonPath("$.data.statusDistribution[0].statusName").value("待付款"))
                .andExpect(jsonPath("$.data.statusDistribution[4].statusName").value("已取消"))
                .andExpect(jsonPath("$.data.payTypeDistribution").isArray());
    }

    @Test
    void orderStatisticsShouldRespectDateRangeAndValidateParams() throws Exception {
        String token = obtainAdminToken();

        // 未来日期区间没有订单，核心指标为 0，但状态分布仍固定 5 项
        mockMvc.perform(get("/api/admin/stats/orders")
                        .header("Authorization", "Bearer " + token)
                        .param("startDate", "2099-01-01")
                        .param("endDate", "2099-01-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalOrderCount").value(0))
                .andExpect(jsonPath("$.data.validOrderCount").value(0))
                .andExpect(jsonPath("$.data.totalPayAmount").value(0))
                .andExpect(jsonPath("$.data.averageOrderAmount").value(0))
                .andExpect(jsonPath("$.data.statusDistribution.length()").value(5))
                .andExpect(jsonPath("$.data.statusDistribution[0].orderCount").value(0));

        // 开始日期晚于结束日期返回 400
        mockMvc.perform(get("/api/admin/stats/orders")
                        .header("Authorization", "Bearer " + token)
                        .param("startDate", "2026-12-01")
                        .param("endDate", "2026-11-01"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));
    }

    @Test
    void orderStatsShouldCoverRecentTimeWindows() throws Exception {
        String token = obtainAdminToken();
        LocalDate today = LocalDate.now();

        // 三个窗口：近24小时（今天）、近一周、近一个月；日期均按含当日处理
        JsonNode day = orderStats(token, today, today);
        JsonNode week = orderStats(token, today.minusDays(6), today);
        JsonNode month = orderStats(token, today.minusDays(29), today);

        long dayCount = day.path("totalOrderCount").asLong();
        long weekCount = week.path("totalOrderCount").asLong();
        long monthCount = month.path("totalOrderCount").asLong();

        // 每个窗口都有数据；窗口越大订单数与有效订单数单调不减
        assertTrue(dayCount >= 3, "近24小时应含今天的多笔订单");
        assertTrue(weekCount > dayCount, "近一周订单数应多于近24小时");
        assertTrue(monthCount > weekCount, "近一个月订单数应多于近一周");
        assertTrue(day.path("validOrderCount").asLong() >= 2);
        assertTrue(week.path("validOrderCount").asLong() >= day.path("validOrderCount").asLong());
        assertTrue(month.path("validOrderCount").asLong() >= week.path("validOrderCount").asLong());

        // 状态分布恒为 5 项，且各项数量之和等于窗口订单总数
        assertStatusDistribution(day, dayCount);
        assertStatusDistribution(week, weekCount);
        assertStatusDistribution(month, monthCount);

        // 金额随窗口扩大单调不减
        assertTrue(month.path("totalPayAmount").decimalValue().compareTo(
                week.path("totalPayAmount").decimalValue()) >= 0);
        assertTrue(week.path("totalPayAmount").decimalValue().compareTo(
                day.path("totalPayAmount").decimalValue()) >= 0);

        // 超过一个月的订单（35 天前）不计入近一个月，但计入更宽区间
        JsonNode beyondMonth = orderStats(token, today.minusDays(40), today.minusDays(31));
        assertEquals(1, beyondMonth.path("totalOrderCount").asLong());
        assertTrue(beyondMonth.path("totalPayAmount").decimalValue().compareTo(BigDecimal.ZERO) > 0);

        // 单独锁定 22 天前的订单：金额 129，归属"近一月-已完成"，验证分层落点准确
        JsonNode day22 = orderStats(token, today.minusDays(22), today.minusDays(22));
        assertEquals(1, day22.path("totalOrderCount").asLong());
        assertEquals(0, new BigDecimal("129.00").compareTo(day22.path("totalPayAmount").decimalValue()));
    }

    private JsonNode orderStats(String token, LocalDate startDate, LocalDate endDate) throws Exception {
        MvcResult result = mockMvc.perform(get("/api/admin/stats/orders")
                        .header("Authorization", "Bearer " + token)
                        .param("startDate", startDate.toString())
                        .param("endDate", endDate.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString()).path("data");
    }

    private void assertStatusDistribution(JsonNode stats, long expectedTotal) {
        JsonNode distribution = stats.path("statusDistribution");
        assertEquals(5, distribution.size());
        long sum = 0;
        for (JsonNode item : distribution) {
            sum += item.path("orderCount").asLong();
        }
        assertEquals(expectedTotal, sum);
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
