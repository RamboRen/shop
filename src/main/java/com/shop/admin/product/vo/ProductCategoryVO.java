package com.shop.admin.product.vo;

import com.shop.admin.common.enums.CommonStatus;
import com.shop.admin.product.entity.ProductCategoryDO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 商品分类视图对象。
 *
 * @author shop-admin
 */
@Getter
@Setter
@Schema(description = "商品分类信息")
public class ProductCategoryVO {

    @Schema(description = "分类编号", example = "1")
    private Long id;

    @Schema(description = "分类名称", example = "数码家电")
    private String name;

    @Schema(description = "排序值", example = "1")
    private Integer sort;

    @Schema(description = "状态，0 禁用，1 启用", example = "1")
    private Integer status;

    @Schema(description = "状态名称", example = "启用")
    private String statusName;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    /**
     * 将数据对象转换为视图对象。
     *
     * @param categoryDO 分类数据对象
     * @return 分类视图对象
     */
    public static ProductCategoryVO from(ProductCategoryDO categoryDO) {
        ProductCategoryVO categoryVO = new ProductCategoryVO();
        categoryVO.setId(categoryDO.getId());
        categoryVO.setName(categoryDO.getName());
        categoryVO.setSort(categoryDO.getSort());
        categoryVO.setStatus(categoryDO.getStatus());
        categoryVO.setStatusName(CommonStatus.fromCode(categoryDO.getStatus()).getDescription());
        categoryVO.setCreateTime(categoryDO.getCreateTime());
        categoryVO.setUpdateTime(categoryDO.getUpdateTime());
        return categoryVO;
    }
}
