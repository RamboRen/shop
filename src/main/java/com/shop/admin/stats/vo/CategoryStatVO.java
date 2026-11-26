package com.shop.admin.stats.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * 分类商品分布视图对象。
 *
 * @author shop-admin
 */
@Getter
@Setter
@Schema(description = "分类商品分布")
public class CategoryStatVO {

    @Schema(description = "分类名称", example = "数码家电")
    private String categoryName;

    @Schema(description = "商品数量", example = "12")
    private Long productCount;

    /**
     * 构造分类分布视图对象。
     *
     * @param categoryName 分类名称
     * @param productCount 商品数量
     */
    public CategoryStatVO(String categoryName, Long productCount) {
        this.categoryName = categoryName;
        this.productCount = productCount;
    }
}
