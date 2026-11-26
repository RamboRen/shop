package com.shop.admin.user.service;

import com.shop.admin.common.api.PageResult;
import com.shop.admin.user.dto.UserCreateRequest;
import com.shop.admin.user.dto.UserQueryRequest;
import com.shop.admin.user.dto.UserStatusRequest;
import com.shop.admin.user.dto.UserUpdateRequest;
import com.shop.admin.user.vo.UserDetailVO;
import com.shop.admin.user.vo.UserVO;

/**
 * 商城用户服务。
 *
 * @author shop-admin
 */
public interface UserService {

    /**
     * 分页查询用户。
     *
     * @param request 查询条件
     * @return 用户分页结果
     */
    PageResult<UserVO> pageUsers(UserQueryRequest request);

    /**
     * 查询用户详情，包含订单统计。
     *
     * @param id 用户编号
     * @return 用户详情
     */
    UserDetailVO getUser(Long id);

    /**
     * 新增用户。
     *
     * @param request 新增请求
     * @return 新用户编号
     */
    Long createUser(UserCreateRequest request);

    /**
     * 修改用户。
     *
     * @param id      用户编号
     * @param request 修改请求
     */
    void updateUser(Long id, UserUpdateRequest request);

    /**
     * 删除用户。
     *
     * @param id 用户编号
     */
    void deleteUser(Long id);

    /**
     * 修改用户启用状态。
     *
     * @param id      用户编号
     * @param request 状态请求
     */
    void updateStatus(Long id, UserStatusRequest request);
}
