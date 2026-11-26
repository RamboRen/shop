package com.shop.admin.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shop.admin.order.entity.OrderItemDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单明细 Mapper。
 *
 * @author shop-admin
 */
@Mapper
public interface OrderItemMapper extends BaseMapper<OrderItemDO> {
}
