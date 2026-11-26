package com.shop.admin.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shop.admin.product.entity.ProductCategoryDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品分类数据访问接口。
 *
 * @author shop-admin
 */
@Mapper
public interface ProductCategoryMapper extends BaseMapper<ProductCategoryDO> {
}
