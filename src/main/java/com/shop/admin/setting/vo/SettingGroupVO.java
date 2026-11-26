package com.shop.admin.setting.vo;

import com.shop.admin.common.enums.SettingGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 设置分组视图对象，包含分组下的全部配置项。
 *
 * @author shop-admin
 */
@Getter
@Setter
@Schema(description = "设置分组")
public class SettingGroupVO {

    @Schema(description = "分组编码", example = "BASIC")
    private String groupCode;

    @Schema(description = "分组名称", example = "基础设置")
    private String groupName;

    @Schema(description = "分组下的配置项")
    private List<SettingVO> items;

    /**
     * 根据分组枚举与配置项构造视图对象。
     *
     * @param group 分组枚举
     * @param items 配置项列表
     * @return 分组视图对象
     */
    public static SettingGroupVO of(SettingGroup group, List<SettingVO> items) {
        SettingGroupVO vo = new SettingGroupVO();
        vo.setGroupCode(group.getCode());
        vo.setGroupName(group.getDescription());
        vo.setItems(items);
        return vo;
    }
}
