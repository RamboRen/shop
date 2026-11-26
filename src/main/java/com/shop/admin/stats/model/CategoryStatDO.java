package com.shop.admin.stats.model;

import lombok.Getter;
import lombok.Setter;

/**
 * 分类商品分布数据对象。
 *
 * @author shop-admin
 */
@Getter
@Setter
public class CategoryStatDO {

    /**
     * 分类名称。
     */
    private String categoryName;

    /**
     * 商品数量。
     */
    private Long productCount;
}
