package com.shop.admin.coupon.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shop.admin.coupon.entity.CouponDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券数据访问接口。
 *
 * @author shop-admin
 */
@Mapper
public interface CouponMapper extends BaseMapper<CouponDO> {
}
