package com.shop.admin.config;

import com.shop.admin.common.constant.SecurityConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Web MVC 静态资源映射配置。
 *
 * @author shop-admin
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final UploadProperties uploadProperties;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path uploadRoot = Paths.get(uploadProperties.getPath()).toAbsolutePath().normalize();
        String resourceLocation = uploadRoot.toUri().toString();
        if (!resourceLocation.endsWith("/")) {
            resourceLocation = resourceLocation + "/";
        }
        registry.addResourceHandler(SecurityConstants.UPLOAD_PATH_PATTERN)
                .addResourceLocations(resourceLocation)
                .setCachePeriod(3600);
        log.info("图片静态资源映射：{} -> {}（上传目录：{}）",
                SecurityConstants.UPLOAD_PATH_PATTERN, resourceLocation, uploadRoot);
    }
}
