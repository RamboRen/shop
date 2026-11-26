package com.shop.admin.gallery.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 图库批量删除请求。
 *
 * @author shop-admin
 */
@Getter
@Setter
@Schema(description = "图库批量删除请求")
public class GalleryBatchDeleteRequest {

    @NotEmpty(message = "图片编号列表不能为空")
    @Size(max = 50, message = "单次最多删除 50 张图片")
    @Schema(description = "图片编号列表")
    private List<@NotNull(message = "图片编号不能为空")
            @Positive(message = "图片编号必须大于 0") Long> ids;
}
