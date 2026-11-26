package com.shop.admin;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Map;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 图库分类管理集成测试。
 *
 * @author shop-admin
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class GalleryCategoryIntegrationTest {

    private static final String BASE_PATH = "/api/admin/gallery/categories";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldListDefaultCategories() throws Exception {
        String token = obtainToken("admin");
        MvcResult result = mockMvc.perform(get(BASE_PATH)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andReturn();
        JsonNode data = objectMapper.readTree(result.getResponse().getContentAsString()).path("data");

        // 测试共享同一 H2 库且无事务回滚，故只校验内置分类存在，不断言总数
        Map<String, String> defaults = Map.of(
                "PRODUCT", "商品图", "BANNER", "轮播图", "AVATAR", "头像", "OTHER", "其他");
        for (Map.Entry<String, String> entry : defaults.entrySet()) {
            JsonNode matched = null;
            for (JsonNode item : data) {
                if (entry.getKey().equals(item.path("categoryCode").asText())) {
                    matched = item;
                    break;
                }
            }
            org.junit.jupiter.api.Assertions.assertNotNull(matched, "应包含内置分类: " + entry.getKey());
            org.junit.jupiter.api.Assertions.assertEquals(entry.getValue(),
                    matched.path("categoryName").asText());
            org.junit.jupiter.api.Assertions.assertEquals("启用", matched.path("statusName").asText());
            org.junit.jupiter.api.Assertions.assertTrue(matched.path("imageCount").isNumber());
        }

        // operator 拥有 gallery:view 权限，可以访问分类管理
        String operatorToken = obtainToken("operator");
        mockMvc.perform(get(BASE_PATH)
                        .header("Authorization", "Bearer " + operatorToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void shouldCreateUpdateAndDeleteCategory() throws Exception {
        String token = obtainToken("admin");
        String code = randomCode();
        String name = "测试分类-" + code;

        MvcResult createResult = mockMvc.perform(post(BASE_PATH)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "categoryCode", code,
                                "categoryName", name,
                                "sort", 9,
                                "status", 1,
                                "remark", "集成测试创建"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andReturn();
        long id = readDataId(createResult);

        mockMvc.perform(get(BASE_PATH + "/" + id)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.categoryCode").value(code))
                .andExpect(jsonPath("$.data.categoryName").value(name))
                .andExpect(jsonPath("$.data.sort").value(9))
                .andExpect(jsonPath("$.data.status").value(1));

        mockMvc.perform(put(BASE_PATH + "/" + id)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                            "categoryCode", code,
                                "categoryName", name + "-改",
                                "sort", 8,
                                "status", 0,
                                "remark", "已修改"))))
                .andExpect(status().isOk());

        mockMvc.perform(get(BASE_PATH + "/" + id)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.categoryName").value(name + "-改"))
                .andExpect(jsonPath("$.data.sort").value(8))
                .andExpect(jsonPath("$.data.status").value(0))
                .andExpect(jsonPath("$.data.statusName").value("禁用"));

        // 状态筛选：禁用列表应包含刚创建的分类
        MvcResult disabledResult = mockMvc.perform(get(BASE_PATH)
                        .header("Authorization", "Bearer " + token)
                        .param("status", "0"))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode disabledList = objectMapper.readTree(
                disabledResult.getResponse().getContentAsString()).path("data");
        boolean found = false;
        for (JsonNode item : disabledList) {
            if (id == item.path("id").asLong()) {
                found = true;
                break;
            }
        }
        org.junit.jupiter.api.Assertions.assertTrue(found, "禁用分类列表应包含新建分类");

        mockMvc.perform(delete(BASE_PATH + "/" + id)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        mockMvc.perform(get(BASE_PATH + "/" + id)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404));
    }

    @Test
    void shouldRejectDuplicateAndInvalidCategory() throws Exception {
        String token = obtainToken("admin");
        String code = randomCode();
        String name = "重复测试-" + code;
        createCategory(token, code, name);

        long id = findCategoryId(token, code);

        try {
            // 编码重复
            mockMvc.perform(post(BASE_PATH)
                            .header("Authorization", "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(Map.of(
                                    "categoryCode", code,
                                    "categoryName", "另一个名称-" + code,
                                    "sort", 1, "status", 1))))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.code").value(409));

            // 名称重复
            mockMvc.perform(post(BASE_PATH)
                            .header("Authorization", "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(Map.of(
                                    "categoryCode", randomCode(),
                                    "categoryName", name,
                                    "sort", 1, "status", 1))))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.code").value(409));

            // 非法编码（小写、含连字符）
            mockMvc.perform(post(BASE_PATH)
                            .header("Authorization", "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(Map.of(
                                    "categoryCode", "bad-code",
                                    "categoryName", "非法编码",
                                    "sort", 1, "status", 1))))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400));

            // 非法状态
            mockMvc.perform(post(BASE_PATH)
                            .header("Authorization", "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(Map.of(
                                    "categoryCode", randomCode(),
                                    "categoryName", "非法状态",
                                    "sort", 1, "status", 9))))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400));
        } finally {
            if (id > 0) {
                mockMvc.perform(delete(BASE_PATH + "/" + id)
                        .header("Authorization", "Bearer " + token));
            }
        }
    }

    @Test
    void shouldProtectCategoryWithImagesAndUseCustomCategory() throws Exception {
        String token = obtainToken("admin");
        String code = randomCode();
        String name = "带图分类-" + code;
        createCategory(token, code, name);
        long categoryId = findCategoryId(token, code);

        Long imageId = null;
        try {
            MockMultipartFile imageFile = new MockMultipartFile(
                    "file", "cat-" + code + ".png", "image/png", createPngBytes());
            MvcResult uploadResult = mockMvc.perform(multipart("/api/admin/gallery/images")
                            .file(imageFile)
                            .param("categoryCode", code)
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.imageGroup").value(code))
                    .andExpect(jsonPath("$.data.imageGroupName").value(name))
                    .andReturn();
            imageId = readDataId(uploadResult);

            // 自定义分类下的图片可按分类编码筛选，且展示自定义分类名
            mockMvc.perform(get("/api/admin/gallery/images")
                            .header("Authorization", "Bearer " + token)
                            .param("imageGroup", code))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.records[0].imageGroup").value(code))
                    .andExpect(jsonPath("$.data.records[0].imageGroupName").value(name));

            // 分类下有图片，禁止删除
            mockMvc.perform(delete(BASE_PATH + "/" + categoryId)
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400));

            // 禁用后不能上传到该分类
            mockMvc.perform(put(BASE_PATH + "/" + categoryId)
                            .header("Authorization", "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(Map.of(
                                "categoryCode", code,
                                    "categoryName", name, "sort", 9, "status", 0))))
                    .andExpect(status().isOk());
            mockMvc.perform(multipart("/api/admin/gallery/images")
                            .file(new MockMultipartFile("file", "blocked.png", "image/png", createPngBytes()))
                            .param("categoryCode", code)
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400));
        } finally {
            if (imageId != null) {
                mockMvc.perform(delete("/api/admin/gallery/images/" + imageId)
                        .header("Authorization", "Bearer " + token));
            }
            // 图片删除后再删分类
            mockMvc.perform(delete(BASE_PATH + "/" + categoryId)
                    .header("Authorization", "Bearer " + token));
        }
    }

    @Test
    void shouldQueryImagesByCategoryIncludingDisabled() throws Exception {
        String token = obtainToken("admin");
        String code = randomCode();
        String name = "分类查询-" + code;
        createCategory(token, code, name);
        long categoryId = findCategoryId(token, code);

        Long imageId = null;
        try {
            MockMultipartFile imageFile = new MockMultipartFile(
                    "file", "by-cat-" + code + ".png", "image/png", createPngBytes());
            MvcResult uploadResult = mockMvc.perform(multipart("/api/admin/gallery/images")
                            .file(imageFile)
                            .param("categoryCode", code)
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk())
                    .andReturn();
            imageId = readDataId(uploadResult);

            // 启用状态下按分类编码查询
            mockMvc.perform(get("/api/admin/gallery/images/category/" + code)
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.total").value(1))
                    .andExpect(jsonPath("$.data.records[0].imageGroup").value(code))
                    .andExpect(jsonPath("$.data.records[0].imageGroupName").value(name));

            // 支持关键字进一步筛选
            mockMvc.perform(get("/api/admin/gallery/images/category/" + code)
                            .header("Authorization", "Bearer " + token)
                            .param("keyword", "not-exist-image"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.total").value(0));

            // 禁用分类后，历史图片仍可按分类查到，且分类名不退化成编码
            mockMvc.perform(put(BASE_PATH + "/" + categoryId)
                            .header("Authorization", "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(Map.of(
                                "categoryCode", code,
                                    "categoryName", name, "sort", 9, "status", 0))))
                    .andExpect(status().isOk());

            mockMvc.perform(get("/api/admin/gallery/images/category/" + code)
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.total").value(1))
                    .andExpect(jsonPath("$.data.records[0].imageGroupName").value(name));

            // 对比：旧接口用 imageGroup 筛选已禁用分类会报 400，新接口补上了这个盲区
            mockMvc.perform(get("/api/admin/gallery/images")
                            .header("Authorization", "Bearer " + token)
                            .param("imageGroup", code))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400));

            // 分类不存在返回 404
            mockMvc.perform(get("/api/admin/gallery/images/category/" + randomCode())
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value(404));

            // 未认证拦截
            mockMvc.perform(get("/api/admin/gallery/images/category/" + code))
                    .andExpect(status().isUnauthorized());
        } finally {
            if (imageId != null) {
                mockMvc.perform(delete("/api/admin/gallery/images/" + imageId)
                        .header("Authorization", "Bearer " + token));
            }
            mockMvc.perform(delete(BASE_PATH + "/" + categoryId)
                    .header("Authorization", "Bearer " + token));
        }
    }

    @Test
    void shouldRejectUnauthenticated() throws Exception {
        mockMvc.perform(get(BASE_PATH))
                .andExpect(status().isUnauthorized());
    }

    private void createCategory(String token, String code, String name) throws Exception {
        mockMvc.perform(post(BASE_PATH)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "categoryCode", code,
                                "categoryName", name,
                                "sort", 1,
                                "status", 1))))
                .andExpect(status().isOk());
    }

    private long findCategoryId(String token, String code) throws Exception {
        MvcResult result = mockMvc.perform(get(BASE_PATH)
                        .header("Authorization", "Bearer " + token))
                .andReturn();
        JsonNode data = objectMapper.readTree(result.getResponse().getContentAsString()).path("data");
        for (JsonNode item : data) {
            if (code.equals(item.path("categoryCode").asText())) {
                return item.path("id").asLong();
            }
        }
        throw new IllegalStateException("分类未找到: " + code);
    }

    private long readDataId(MvcResult result) throws Exception {
        return objectMapper.readTree(result.getResponse().getContentAsString())
                .path("data").asLong();
    }

    private String randomCode() {
        return "T" + UUID.randomUUID().toString().replace("-", "")
                .substring(0, 9).toUpperCase();
    }

    private byte[] createPngBytes() throws Exception {
        BufferedImage image = new BufferedImage(4, 4, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "png", outputStream);
        return outputStream.toByteArray();
    }

    private String obtainToken(String username) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/admin/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                Map.of("username", username, "password", "admin123"))))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode response = objectMapper.readTree(result.getResponse().getContentAsString());
        return response.path("data").path("token").asText();
    }
}
