package com.shop.admin.admin.controller;

import com.shop.admin.admin.dto.AdminCreateRequest;
import com.shop.admin.admin.dto.AdminPasswordResetRequest;
import com.shop.admin.admin.dto.AdminQueryRequest;
import com.shop.admin.admin.dto.AdminStatusRequest;
import com.shop.admin.admin.dto.AdminUpdateRequest;
import com.shop.admin.admin.service.AdminService;
import com.shop.admin.admin.vo.AdminVO;
import com.shop.admin.common.api.ApiResult;
import com.shop.admin.common.api.PageResult;
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
 * 管理员管理接口，超级管理员或拥有 system:admin 菜单权限的管理员可访问。
 *
 * @author shop-admin
 */
@RestController
@RequestMapping("/api/admin/admins")
@RequiredArgsConstructor
@PreAuthorize("hasRole('SUPER_ADMIN') or hasAuthority('system:admin')")
@Tag(name = "管理员管理", description = "管理员的查询、新增、修改、启停与密码重置")
@SecurityRequirement(name = "bearerAuth")
public class AdminController {

    private final AdminService adminService;

    /**
     * 分页查询管理员。
     *
     * @param request 查询条件
     * @return 管理员分页结果
     */
    @GetMapping
    @Operation(summary = "分页查询管理员")
    public ApiResult<PageResult<AdminVO>> pageAdmins(@Valid @ParameterObject AdminQueryRequest request) {
        return ApiResult.success(adminService.pageAdmins(request));
    }

    /**
     * 查询管理员详情。
     *
     * @param id 管理员编号
     * @return 管理员信息
     */
    @GetMapping("/{id}")
    @Operation(summary = "查询管理员详情")
    public ApiResult<AdminVO> getAdmin(@PathVariable Long id) {
        return ApiResult.success(adminService.getAdmin(id));
    }

    /**
     * 新增管理员。
     *
     * @param request 新增请求
     * @return 新管理员编号
     */
    @PostMapping
    @Operation(summary = "新增管理员")
    public ApiResult<Long> createAdmin(@Valid @RequestBody AdminCreateRequest request) {
        return ApiResult.success(adminService.createAdmin(request));
    }

    /**
     * 修改管理员。
     *
     * @param id      管理员编号
     * @param request 修改请求
     * @return 操作结果
     */
    @PutMapping("/{id}")
    @Operation(summary = "修改管理员")
    public ApiResult<Void> updateAdmin(@PathVariable Long id,
                                       @Valid @RequestBody AdminUpdateRequest request) {
        adminService.updateAdmin(id, request);
        return ApiResult.success();
    }

    /**
     * 删除管理员。
     *
     * @param id 管理员编号
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除管理员")
    public ApiResult<Void> deleteAdmin(@PathVariable Long id) {
        adminService.deleteAdmin(id);
        return ApiResult.success();
    }

    /**
     * 修改管理员启用状态。
     *
     * @param id      管理员编号
     * @param request 状态请求
     * @return 操作结果
     */
    @PutMapping("/{id}/status")
    @Operation(summary = "修改管理员启用状态")
    public ApiResult<Void> updateStatus(@PathVariable Long id,
                                        @Valid @RequestBody AdminStatusRequest request) {
        adminService.updateStatus(id, request);
        return ApiResult.success();
    }

    /**
     * 重置管理员密码。
     *
     * @param id      管理员编号
     * @param request 重置密码请求
     * @return 操作结果
     */
    @PutMapping("/{id}/password")
    @Operation(summary = "重置管理员密码")
    public ApiResult<Void> resetPassword(@PathVariable Long id,
                                         @Valid @RequestBody AdminPasswordResetRequest request) {
        adminService.resetPassword(id, request);
        return ApiResult.success();
    }
}
