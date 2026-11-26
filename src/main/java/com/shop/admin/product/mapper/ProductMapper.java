package com.shop.admin.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shop.admin.product.entity.ProductDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品数据访问接口。
 *
 * @author shop-admin
 */
@Mapper
public interface ProductMapper extends BaseMapper<ProductDO> {
}
