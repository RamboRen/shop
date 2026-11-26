package com.shop.admin.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shop.admin.admin.entity.AdminDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 管理员数据访问接口。
 *
 * @author shop-admin
 */
@Mapper
public interface AdminMapper extends BaseMapper<AdminDO> {
}
