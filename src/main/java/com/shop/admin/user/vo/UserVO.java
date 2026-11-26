package com.shop.admin.user.vo;

import com.shop.admin.common.enums.CommonStatus;
import com.shop.admin.common.enums.Gender;
import com.shop.admin.user.entity.UserDO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用户信息视图对象。
 *
 * @author shop-admin
 */
@Getter
@Setter
@Schema(description = "用户信息")
public class UserVO {

    @Schema(description = "用户编号", example = "1")
    private Long id;

    @Schema(description = "用户编号", example = "U20260001")
    private String userNo;

    @Schema(description = "登录账号", example = "zhangsan")
    private String username;

    @Schema(description = "昵称", example = "张三")
    private String nickname;

    @Schema(description = "手机号", example = "13800138000")
    private String phone;

    @Schema(description = "邮箱", example = "user@example.com")
    private String email;

    @Schema(description = "头像地址")
    private String avatar;

    @Schema(description = "性别编码", example = "MALE")
    private String gender;

    @Schema(description = "性别名称", example = "男")
    private String genderName;

    @Schema(description = "出生日期")
    private LocalDate birthday;

    @Schema(description = "状态，0 禁用，1 启用", example = "1")
    private Integer status;

    @Schema(description = "状态名称", example = "启用")
    private String statusName;

    @Schema(description = "最近登录时间")
    private LocalDateTime lastLoginTime;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    /**
     * 将数据对象转换为视图对象。
     *
     * @param userDO 用户数据对象
     * @return 用户视图对象
     */
    public static UserVO from(UserDO userDO) {
        UserVO userVO = new UserVO();
        userVO.copyFrom(userDO);
        return userVO;
    }

    /**
     * 复制用户基础字段。
     *
     * @param userDO 用户数据对象
     */
    protected void copyFrom(UserDO userDO) {
        setId(userDO.getId());
        setUserNo(userDO.getUserNo());
        setUsername(userDO.getUsername());
        setNickname(userDO.getNickname());
        setPhone(userDO.getPhone());
        setEmail(userDO.getEmail());
        setAvatar(userDO.getAvatar());
        setGender(userDO.getGender());
        setGenderName(Gender.fromCode(userDO.getGender()).getDescription());
        setBirthday(userDO.getBirthday());
        setStatus(userDO.getStatus());
        setStatusName(CommonStatus.fromCode(userDO.getStatus()).getDescription());
        setLastLoginTime(userDO.getLastLoginTime());
        setRemark(userDO.getRemark());
        setCreateTime(userDO.getCreateTime());
        setUpdateTime(userDO.getUpdateTime());
    }
}
