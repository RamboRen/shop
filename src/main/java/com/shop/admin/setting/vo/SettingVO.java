package com.shop.admin.setting.vo;

import com.shop.admin.common.enums.SettingGroup;
import com.shop.admin.common.enums.SettingValueType;
import com.shop.admin.setting.entity.SettingDO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 系统设置项视图对象。
 *
 * @author shop-admin
 */
@Getter
@Setter
@Schema(description = "系统设置项")
public class SettingVO {

    @Schema(description = "主键", example = "1")
    private Long id;

    @Schema(description = "配置键", example = "site.name")
    private String configKey;

    @Schema(description = "配置值", example = "云商城")
    private String configValue;

    @Schema(description = "配置分组编码", example = "BASIC")
    private String configGroup;

    @Schema(description = "配置分组名称", example = "基础设置")
    private String configGroupName;

    @Schema(description = "配置显示名称", example = "站点名称")
    private String configName;

    @Schema(description = "值类型编码", example = "STRING")
    private String valueType;

    @Schema(description = "值类型名称", example = "文本")
    private String valueTypeName;

    @Schema(description = "排序值", example = "1")
    private Integer sort;

    @Schema(description = "备注说明")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    /**
     * 将数据对象转换为视图对象。
     *
     * @param settingDO 设置数据对象
     * @return 设置视图对象
     */
    public static SettingVO from(SettingDO settingDO) {
        SettingVO vo = new SettingVO();
        vo.setId(settingDO.getId());
        vo.setConfigKey(settingDO.getConfigKey());
        vo.setConfigValue(settingDO.getConfigValue());
        vo.setConfigGroup(settingDO.getConfigGroup());
        try {
            vo.setConfigGroupName(SettingGroup.fromCode(settingDO.getConfigGroup()).getDescription());
        } catch (IllegalArgumentException ignored) {
            vo.setConfigGroupName(settingDO.getConfigGroup());
        }
        vo.setConfigName(settingDO.getConfigName());
        vo.setValueType(settingDO.getValueType());
        try {
            vo.setValueTypeName(SettingValueType.fromCode(settingDO.getValueType()).getDescription());
        } catch (IllegalArgumentException ignored) {
            vo.setValueTypeName(settingDO.getValueType());
        }
        vo.setSort(settingDO.getSort());
        vo.setRemark(settingDO.getRemark());
        vo.setCreateTime(settingDO.getCreateTime());
        vo.setUpdateTime(settingDO.getUpdateTime());
        return vo;
    }
}
