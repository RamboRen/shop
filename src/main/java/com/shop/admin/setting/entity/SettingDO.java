package com.shop.admin.setting.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.shop.admin.common.constant.CommonConstants;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 系统设置项数据对象，采用 key-value 形式存储。
 *
 * @author shop-admin
 */
@Getter
@Setter
@TableName("sys_setting")
public class SettingDO {

    /**
     * 主键。
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 配置键，全局唯一，如 site.name、contact.phone。
     */
    private String configKey;

    /**
     * 配置值。
     */
    private String configValue;

    /**
     * 配置分组，见 SettingGroup。
     */
    private String configGroup;

    /**
     * 配置显示名称。
     */
    private String configName;

    /**
     * 值类型，见 SettingValueType。
     */
    private String valueType;

    /**
     * 排序值。
     */
    private Integer sort;

    /**
     * 备注说明。
     */
    private String remark;

    /**
     * 创建时间。
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间。
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 逻辑删除标识，0 未删除，1 已删除。
     */
    @TableLogic
    private Integer deleted = CommonConstants.NOT_DELETED;
}
