package com.shop.admin.menu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shop.admin.menu.entity.RoleMenuDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 角色菜单关联数据访问接口。
 *
 * @author shop-admin
 */
@Mapper
public interface RoleMenuMapper extends BaseMapper<RoleMenuDO> {
}
