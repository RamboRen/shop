package com.shop.admin.menu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shop.admin.menu.entity.MenuDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 后台菜单数据访问接口。
 *
 * @author shop-admin
 */
@Mapper
public interface MenuMapper extends BaseMapper<MenuDO> {
}
