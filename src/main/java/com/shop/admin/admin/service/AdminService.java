package com.shop.admin.admin.service;

import com.shop.admin.admin.dto.AdminCreateRequest;
import com.shop.admin.admin.dto.AdminPasswordResetRequest;
import com.shop.admin.admin.dto.AdminQueryRequest;
import com.shop.admin.admin.dto.AdminStatusRequest;
import com.shop.admin.admin.dto.AdminUpdateRequest;
import com.shop.admin.admin.vo.AdminVO;
import com.shop.admin.common.api.PageResult;

/**
 * 管理员管理服务接口。
 *
 * @author shop-admin
 */
public interface AdminService {

    /**
     * 分页查询管理员。
     *
     * @param request 查询条件
     * @return 管理员分页结果
     */
    PageResult<AdminVO> pageAdmins(AdminQueryRequest request);

    /**
     * 查询管理员详情。
     *
     * @param id 管理员编号
     * @return 管理员信息
     */
    AdminVO getAdmin(Long id);

    /**
     * 新增管理员。
     *
     * @param request 新增请求
     * @return 新管理员编号
     */
    Long createAdmin(AdminCreateRequest request);

    /**
     * 修改管理员。
     *
     * @param id      管理员编号
     * @param request 修改请求
     */
    void updateAdmin(Long id, AdminUpdateRequest request);

    /**
     * 删除管理员。
     *
     * @param id 管理员编号
     */
    void deleteAdmin(Long id);

    /**
     * 修改管理员启用状态。
     *
     * @param id      管理员编号
     * @param request 状态修改请求
     */
    void updateStatus(Long id, AdminStatusRequest request);

    /**
     * 重置管理员密码。
     *
     * @param id      管理员编号
     * @param request 重置密码请求
     */
    void resetPassword(Long id, AdminPasswordResetRequest request);
}
