package com.shop.admin.setting.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shop.admin.setting.entity.SettingDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系统设置 Mapper。
 *
 * @author shop-admin
 */
@Mapper
public interface SettingMapper extends BaseMapper<SettingDO> {
}
