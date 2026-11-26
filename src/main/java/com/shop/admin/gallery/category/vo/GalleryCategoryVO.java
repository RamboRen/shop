package com.shop.admin.gallery.category.vo;

import com.shop.admin.common.enums.CommonStatus;
import com.shop.admin.gallery.category.entity.GalleryCategoryDO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 图库分类视图对象。
 *
 * @author shop-admin
 */
@Getter
@Setter
@Schema(description = "图库分类信息")
public class GalleryCategoryVO {

    @Schema(description = "分类编号", example = "1")
    private Long id;

    @Schema(description = "分类编码", example = "PRODUCT")
    private String categoryCode;

    @Schema(description = "分类名称", example = "商品图")
    private String categoryName;

    @Schema(description = "排序值", example = "1")
    private Integer sort;

    @Schema(description = "状态，0 禁用，1 启用", example = "1")
    private Integer status;

    @Schema(description = "状态名称", example = "启用")
    private String statusName;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "该分类下的图片数量", example = "12")
    private Long imageCount;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    /**
     * 将数据对象转换为视图对象。
     *
     * @param categoryDO 分类数据对象
     * @param imageCount 分类下的图片数量
     * @return 分类视图对象
     */
    public static GalleryCategoryVO from(GalleryCategoryDO categoryDO, Long imageCount) {
        GalleryCategoryVO categoryVO = new GalleryCategoryVO();
        categoryVO.setId(categoryDO.getId());
        categoryVO.setCategoryCode(categoryDO.getCategoryCode());
        categoryVO.setCategoryName(categoryDO.getCategoryName());
        categoryVO.setSort(categoryDO.getSort());
        categoryVO.setStatus(categoryDO.getStatus());
        categoryVO.setStatusName(CommonStatus.fromCode(categoryDO.getStatus()).getDescription());
        categoryVO.setRemark(categoryDO.getRemark());
        categoryVO.setImageCount(imageCount == null ? 0L : imageCount);
        categoryVO.setCreateTime(categoryDO.getCreateTime());
        categoryVO.setUpdateTime(categoryDO.getUpdateTime());
        return categoryVO;
    }
}
