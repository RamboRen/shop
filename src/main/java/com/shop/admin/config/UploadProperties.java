package com.shop.admin.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.unit.DataSize;

import java.util.List;

/**
 * 文件上传配置属性。
 *
 * @author shop-admin
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "app.upload")
public class UploadProperties {

    /**
     * 上传文件本地根目录。
     */
    private String path = "./uploads";

    /**
     * 单个文件最大大小。
     */
    private DataSize maxFileSize = DataSize.ofMegabytes(10);

    /**
     * 允许上传的图片媒体类型。
     */
    private List<String> allowedContentTypes = List.of("image/jpeg", "image/png", "image/gif");
}
