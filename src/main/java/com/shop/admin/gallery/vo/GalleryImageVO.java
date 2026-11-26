package com.shop.admin.gallery.vo;

import com.shop.admin.gallery.entity.GalleryImageDO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 图库图片视图对象。
 *
 * @author shop-admin
 */
@Getter
@Setter
@Schema(description = "图库图片信息")
public class GalleryImageVO {

    @Schema(description = "图片编号", example = "1")
    private Long id;

    @Schema(description = "图片访问地址", example = "/uploads/2026/11/09/demo.jpg")
    private String url;

    @Schema(description = "原始文件名", example = "product.jpg")
    private String originalName;

    @Schema(description = "媒体类型", example = "image/jpeg")
    private String contentType;

    @Schema(description = "文件大小，单位字节", example = "102400")
    private Long fileSize;

    @Schema(description = "图片宽度", example = "800")
    private Integer width;

    @Schema(description = "图片高度", example = "800")
    private Integer height;

    @Schema(description = "图库分组", example = "PRODUCT")
    private String imageGroup;

    @Schema(description = "图库分组名称", example = "商品图")
    private String imageGroupName;

    @Schema(description = "上传时间")
    private LocalDateTime createTime;

    /**
     * 将数据对象转换为视图对象。
     *
     * @param imageDO 图片数据对象
     * @return 图片视图对象
     */
    public static GalleryImageVO from(GalleryImageDO imageDO) {
        return from(imageDO, null);
    }

    /**
     * 将数据对象转换为视图对象，并填充分类名称。
     *
     * @param imageDO      图片数据对象
     * @param categoryName 分类名称，为空时回退为分类编码
     * @return 图片视图对象
     */
    public static GalleryImageVO from(GalleryImageDO imageDO, String categoryName) {
        GalleryImageVO imageVO = new GalleryImageVO();
        imageVO.setId(imageDO.getId());
        imageVO.setUrl(imageDO.getUrl());
        imageVO.setOriginalName(imageDO.getOriginalName());
        imageVO.setContentType(imageDO.getContentType());
        imageVO.setFileSize(imageDO.getFileSize());
        imageVO.setWidth(imageDO.getWidth());
        imageVO.setHeight(imageDO.getHeight());
        imageVO.setImageGroup(imageDO.getImageGroup());
        imageVO.setImageGroupName(categoryName != null ? categoryName : imageDO.getImageGroup());
        imageVO.setCreateTime(imageDO.getCreateTime());
        return imageVO;
    }
}
