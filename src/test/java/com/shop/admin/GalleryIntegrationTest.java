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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 图库管理集成测试。
 *
 * @author shop-admin
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class GalleryIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldUploadQueryAndDeleteImage() throws Exception {
        String token = obtainAdminToken();
        MockMultipartFile imageFile = new MockMultipartFile("file", "test-product.png",
                "image/png", createPngBytes());
        MvcResult uploadResult = mockMvc.perform(multipart("/api/admin/gallery/images")
                        .file(imageFile)
                        .param("categoryCode", "PRODUCT")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.url").value(org.hamcrest.Matchers.startsWith("/uploads/")))
                .andExpect(jsonPath("$.data.width").value(4))
                .andExpect(jsonPath("$.data.height").value(4))
                .andExpect(jsonPath("$.data.imageGroup").value("PRODUCT"))
                .andReturn();
        JsonNode uploadResponse = objectMapper.readTree(uploadResult.getResponse().getContentAsString());
        long imageId = uploadResponse.path("data").path("id").asLong();

        mockMvc.perform(get("/api/admin/gallery/images")
                        .header("Authorization", "Bearer " + token)
                        .param("keyword", "test-product"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.records[0].id").value(imageId))
                .andExpect(jsonPath("$.data.records[0].imageGroupName").value("商品图"));

        mockMvc.perform(delete("/api/admin/gallery/images/" + imageId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        mockMvc.perform(get("/api/admin/gallery/images")
                        .header("Authorization", "Bearer " + token)
                        .param("keyword", "test-product"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.total").value(0));
    }

    @Test
    void shouldAcceptCategoryCodeParamAndDefault() throws Exception {
        String token = obtainAdminToken();
        long[] uploadedIds = new long[2];

        // categoryCode 参数生效
        MvcResult byCodeResult = mockMvc.perform(multipart("/api/admin/gallery/images")
                        .file(new MockMultipartFile("file", "by-code.png", "image/png", createPngBytes()))
                        .param("categoryCode", "AVATAR")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.imageGroup").value("AVATAR"))
                .andExpect(jsonPath("$.data.imageGroupName").value("头像"))
                .andReturn();
        uploadedIds[0] = objectMapper.readTree(byCodeResult.getResponse().getContentAsString())
                .path("data").path("id").asLong();

        // 不传分类参数时默认 OTHER
        MvcResult defaultResult = mockMvc.perform(multipart("/api/admin/gallery/images")
                        .file(new MockMultipartFile("file", "default-other.png", "image/png", createPngBytes()))
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.imageGroup").value("OTHER"))
                .andExpect(jsonPath("$.data.imageGroupName").value("其他"))
                .andReturn();
        uploadedIds[1] = objectMapper.readTree(defaultResult.getResponse().getContentAsString())
                .path("data").path("id").asLong();

        // 不存在的分类被拒绝
        mockMvc.perform(multipart("/api/admin/gallery/images")
                        .file(new MockMultipartFile("file", "unknown-category.png", "image/png", createPngBytes()))
                        .param("categoryCode", "NOT_EXIST_CATEGORY")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));

        for (long imageId : uploadedIds) {
            mockMvc.perform(delete("/api/admin/gallery/images/" + imageId)
                    .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk());
        }
    }

    @Test
    void shouldRenameImage() throws Exception {
        String token = obtainAdminToken();
        MvcResult uploadResult = mockMvc.perform(multipart("/api/admin/gallery/images")
                        .file(new MockMultipartFile("file", "rename-before.png", "image/png", createPngBytes()))
                        .param("categoryCode", "PRODUCT")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode uploadResponse = objectMapper.readTree(uploadResult.getResponse().getContentAsString());
        long imageId = uploadResponse.path("data").path("id").asLong();
        String originalUrl = uploadResponse.path("data").path("url").asText();

        try {
            // 改名成功，返回新名称，URL 与存储文件名不变
            mockMvc.perform(put("/api/admin/gallery/images/" + imageId + "/name")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(
                                    Map.of("originalName", "首页主图.png")))
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.id").value(imageId))
                    .andExpect(jsonPath("$.data.originalName").value("首页主图.png"))
                    .andExpect(jsonPath("$.data.url").value(originalUrl))
                    .andExpect(jsonPath("$.data.imageGroupName").value("商品图"));

            // 改后按新名称关键字能搜到
            mockMvc.perform(get("/api/admin/gallery/images")
                            .header("Authorization", "Bearer " + token)
                            .param("keyword", "首页主图"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.records[0].id").value(imageId))
                    .andExpect(jsonPath("$.data.records[0].originalName").value("首页主图.png"));

            // 名称中的路径前缀被清洗
            mockMvc.perform(put("/api/admin/gallery/images/" + imageId + "/name")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(
                                    Map.of("originalName", "C:\\fake\\path\\clean-name.png")))
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.originalName").value("clean-name.png"));

            // 空名称校验失败
            mockMvc.perform(put("/api/admin/gallery/images/" + imageId + "/name")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(Map.of("originalName", "   ")))
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400));

            // 不存在的图片 404
            mockMvc.perform(put("/api/admin/gallery/images/99999999/name")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(Map.of("originalName", "whatever.png")))
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value(404));

            // 未认证 401
            mockMvc.perform(put("/api/admin/gallery/images/" + imageId + "/name")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(Map.of("originalName", "no-auth.png"))))
                    .andExpect(status().isUnauthorized());
        } finally {
            mockMvc.perform(delete("/api/admin/gallery/images/" + imageId)
                    .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk());
        }
    }

    @Test
    void shouldRejectNonImageFile() throws Exception {
        String token = obtainAdminToken();
        MockMultipartFile textFile = new MockMultipartFile("file", "not-image.png",
                "image/png", "this is not an image".getBytes());
        mockMvc.perform(multipart("/api/admin/gallery/images")
                        .file(textFile)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));
    }

    private byte[] createPngBytes() throws Exception {
        BufferedImage image = new BufferedImage(4, 4, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "png", outputStream);
        return outputStream.toByteArray();
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
