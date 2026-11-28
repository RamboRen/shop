package com.shop.admin.gallery.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * 修改图片名称请求。
 *
 * @author shop-admin
 */
@Getter
@Setter
@Schema(description = "修改图片名称请求")
public class GalleryImageRenameRequest {

    @NotBlank(message = "图片名称不能为空")
    @Size(max = 255, message = "图片名称不能超过 255 位")
    @Schema(description = "新的图片名称", example = "首页轮播图.png")
    private String originalName;
}
