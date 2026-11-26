package com.shop.admin.user.controller;

import com.shop.admin.common.api.ApiResult;
import com.shop.admin.common.api.PageResult;
import com.shop.admin.user.dto.UserCreateRequest;
import com.shop.admin.user.dto.UserQueryRequest;
import com.shop.admin.user.dto.UserStatusRequest;
import com.shop.admin.user.dto.UserUpdateRequest;
import com.shop.admin.user.service.UserService;
import com.shop.admin.user.vo.UserDetailVO;
import com.shop.admin.user.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 商城用户（会员）管理接口。
 *
 * @author shop-admin
 */
@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('SUPER_ADMIN') or hasAuthority('user:view')")
@Tag(name = "用户管理", description = "商城用户的查询、新增、修改、删除与启停")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;

    /**
     * 分页查询用户。
     *
     * @param request 查询条件
     * @return 用户分页结果
     */
    @GetMapping
    @Operation(summary = "分页查询用户")
    public ApiResult<PageResult<UserVO>> pageUsers(@Valid @ParameterObject UserQueryRequest request) {
        return ApiResult.success(userService.pageUsers(request));
    }

    /**
     * 查询用户详情，包含订单统计。
     *
     * @param id 用户编号
     * @return 用户详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "查询用户详情", description = "返回用户基础信息、订单数量与累计消费")
    public ApiResult<UserDetailVO> getUser(@PathVariable Long id) {
        return ApiResult.success(userService.getUser(id));
    }

    /**
     * 新增用户。
     *
     * @param request 新增请求
     * @return 新用户编号
     */
    @PostMapping
    @Operation(summary = "新增用户")
    public ApiResult<Long> createUser(@Valid @RequestBody UserCreateRequest request) {
        return ApiResult.success(userService.createUser(request));
    }

    /**
     * 修改用户。
     *
     * @param id      用户编号
     * @param request 修改请求
     * @return 操作结果
     */
    @PutMapping("/{id}")
    @Operation(summary = "修改用户")
    public ApiResult<Void> updateUser(@PathVariable Long id,
                                      @Valid @RequestBody UserUpdateRequest request) {
        userService.updateUser(id, request);
        return ApiResult.success();
    }

    /**
     * 删除用户。
     *
     * @param id 用户编号
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除用户")
    public ApiResult<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ApiResult.success();
    }

    /**
     * 修改用户启用状态。
     *
     * @param id      用户编号
     * @param request 状态请求
     * @return 操作结果
     */
    @PutMapping("/{id}/status")
    @Operation(summary = "修改用户启用状态")
    public ApiResult<Void> updateStatus(@PathVariable Long id,
                                        @Valid @RequestBody UserStatusRequest request) {
        userService.updateStatus(id, request);
        return ApiResult.success();
    }
}
