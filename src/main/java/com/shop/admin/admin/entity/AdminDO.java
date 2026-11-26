package com.shop.admin.admin.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.shop.admin.common.constant.CommonConstants;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 管理员数据对象。
 *
 * @author shop-admin
 */
@Getter
@Setter
@TableName("sys_admin")
public class AdminDO {

    /**
     * 主键。
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 登录用户名。
     */
    private String username;

    /**
     * BCrypt 加密后的密码。
     */
    @JsonIgnore
    private String password;

    /**
     * 昵称。
     */
    private String nickname;

    /**
     * 头像地址。
     */
    private String avatar;

    /**
     * 手机号。
     */
    private String phone;

    /**
     * 邮箱。
     */
    private String email;

    /**
     * 角色编码，取值 SUPER_ADMIN 或 ADMIN。
     */
    private String roleCode;

    /**
     * 状态，0 禁用，1 启用。
     */
    private Integer status;

    /**
     * 最近登录时间。
     */
    private LocalDateTime lastLoginTime;

    /**
     * 备注。
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
