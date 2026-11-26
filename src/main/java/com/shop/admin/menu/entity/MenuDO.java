package com.shop.admin.menu.entity;

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
 * 后台菜单数据对象。
 *
 * @author shop-admin
 */
@Getter
@Setter
@TableName("sys_menu")
public class MenuDO {

    /**
     * 主键。
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 父菜单编号，0 表示顶级菜单。
     */
    private Long parentId;

    /**
     * 菜单名称。
     */
    private String menuName;

    /**
     * 菜单类型：CATALOG 目录，MENU 菜单。
     */
    private String menuType;

    /**
     * 前端路由地址。
     */
    private String routePath;

    /**
     * 前端组件路径。
     */
    private String component;

    /**
     * 菜单图标。
     */
    private String icon;

    /**
     * 接口权限编码。
     */
    private String permissionCode;

    /**
     * 排序值。
     */
    private Integer sort;

    /**
     * 是否显示，0 隐藏，1 显示。
     */
    private Integer visible;

    /**
     * 状态，0 禁用，1 启用。
     */
    private Integer status;

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
