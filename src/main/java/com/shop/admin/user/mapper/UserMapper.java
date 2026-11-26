package com.shop.admin.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shop.admin.user.entity.UserDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商城用户 Mapper。
 *
 * @author shop-admin
 */
@Mapper
public interface UserMapper extends BaseMapper<UserDO> {
}
