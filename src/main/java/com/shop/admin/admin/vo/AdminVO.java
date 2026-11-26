package com.shop.admin.admin.vo;

import com.shop.admin.admin.entity.AdminDO;
import com.shop.admin.common.enums.AdminRole;
import com.shop.admin.common.enums.CommonStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 管理员视图对象。
 *
 * @author shop-admin
 */
@Getter
@Setter
@Schema(description = "管理员信息")
public class AdminVO {

    @Schema(description = "管理员编号", example = "1")
    private Long id;

    @Schema(description = "登录用户名", example = "admin")
    private String username;

    @Schema(description = "昵称", example = "超级管理员")
    private String nickname;

    @Schema(description = "头像地址")
    private String avatar;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "角色编码", example = "SUPER_ADMIN")
    private String roleCode;

    @Schema(description = "角色名称", example = "超级管理员")
    private String roleName;

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
     * @param adminDO 管理员数据对象
     * @return 管理员视图对象
     */
    public static AdminVO from(AdminDO adminDO) {
        AdminVO adminVO = new AdminVO();
        adminVO.setId(adminDO.getId());
        adminVO.setUsername(adminDO.getUsername());
        adminVO.setNickname(adminDO.getNickname());
        adminVO.setAvatar(adminDO.getAvatar());
        adminVO.setPhone(adminDO.getPhone());
        adminVO.setEmail(adminDO.getEmail());
        adminVO.setRoleCode(adminDO.getRoleCode());
        adminVO.setRoleName(AdminRole.fromCode(adminDO.getRoleCode()).getDescription());
        adminVO.setStatus(adminDO.getStatus());
        adminVO.setStatusName(CommonStatus.fromCode(adminDO.getStatus()).getDescription());
        adminVO.setLastLoginTime(adminDO.getLastLoginTime());
        adminVO.setRemark(adminDO.getRemark());
        adminVO.setCreateTime(adminDO.getCreateTime());
        adminVO.setUpdateTime(adminDO.getUpdateTime());
        return adminVO;
    }
}
