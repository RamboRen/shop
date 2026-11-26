package com.shop.admin.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shop.admin.order.entity.OrderDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单 Mapper。
 *
 * @author shop-admin
 */
@Mapper
public interface OrderMapper extends BaseMapper<OrderDO> {
}
