package com.shop.admin.spec.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shop.admin.spec.entity.ProductSpecDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品规格数据访问接口。
 *
 * @author shop-admin
 */
@Mapper
public interface ProductSpecMapper extends BaseMapper<ProductSpecDO> {
}
