package com.shop.admin.user.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.shop.admin.common.constant.CommonConstants;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 商城用户（会员）数据对象。
 *
 * @author shop-admin
 */
@Getter
@Setter
@TableName("shop_user")
public class UserDO {

    /**
     * 主键，同时作为订单中的会员编号。
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户编号。
     */
    private String userNo;

    /**
     * 登录账号。
     */
    private String username;

    /**
     * 昵称。
     */
    private String nickname;

    /**
     * 手机号。
     */
    private String phone;

    /**
     * 邮箱。
     */
    private String email;

    /**
     * 头像地址。
     */
    private String avatar;

    /**
     * 性别：UNKNOWN/MALE/FEMALE。
     */
    private String gender;

    /**
     * 出生日期。
     */
    private LocalDate birthday;

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
