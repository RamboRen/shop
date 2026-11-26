package com.shop.admin.gallery.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * 图库分页查询请求。
 *
 * @author shop-admin
 */
@Getter
@Setter
@Schema(description = "图库分页查询请求")
public class GalleryQueryRequest {

    @Min(value = 1, message = "页码不能小于 1")
    @Schema(description = "页码", example = "1")
    private Integer pageNum = 1;

    @Min(value = 1, message = "每页条数不能小于 1")
    @Max(value = 100, message = "每页条数不能超过 100")
    @Schema(description = "每页条数", example = "20")
    private Integer pageSize = 20;

    @Size(max = 100, message = "关键字长度不能超过 100 位")
    @Schema(description = "原始文件名关键字")
    private String keyword;

    @Pattern(regexp = "^$|^[A-Za-z0-9_]{1,30}$", message = "图库分组编码不合法")
    @Schema(description = "图库分组编码", example = "PRODUCT")
    private String imageGroup;
}
