package com.shop.admin.setting.controller;

import com.shop.admin.common.api.ApiResult;
import com.shop.admin.common.api.PageResult;
import com.shop.admin.setting.dto.SettingBatchSaveRequest;
import com.shop.admin.setting.dto.SettingCreateRequest;
import com.shop.admin.setting.dto.SettingQueryRequest;
import com.shop.admin.setting.dto.SettingUpdateRequest;
import com.shop.admin.setting.service.SettingService;
import com.shop.admin.setting.vo.SettingGroupVO;
import com.shop.admin.setting.vo.SettingVO;
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

import java.util.List;
import java.util.Map;

/**
 * 系统设置管理接口。
 *
 * @author shop-admin
 */
@RestController
@RequestMapping("/api/admin/settings")
@RequiredArgsConstructor
@PreAuthorize("hasRole('SUPER_ADMIN') or hasAuthority('system:setting')")
@Tag(name = "系统设置", description = "站点基础信息、联系方式、交易与支付配置的维护")
@SecurityRequirement(name = "bearerAuth")
public class SettingController {

    private final SettingService settingService;

    /**
     * 分页查询设置项。
     *
     * @param request 查询条件
     * @return 设置项分页结果
     */
    @GetMapping
    @Operation(summary = "分页查询设置项")
    public ApiResult<PageResult<SettingVO>> pageSettings(@Valid @ParameterObject SettingQueryRequest request) {
        return ApiResult.success(settingService.pageSettings(request));
    }

    /**
     * 按分组查询全部设置项。
     *
     * @return 分组及配置项列表
     */
    @GetMapping("/groups")
    @Operation(summary = "按分组查询设置项", description = "返回全部设置分组及其下的配置项，供分组编辑页使用")
    public ApiResult<List<SettingGroupVO>> listGroups() {
        return ApiResult.success(settingService.listGroups());
    }

    /**
     * 查询配置键值映射。
     *
     * @return 配置键到配置值的映射
     */
    @GetMapping("/map")
    @Operation(summary = "查询配置映射", description = "返回全部配置的 key-value 映射，供其他模块读取")
    public ApiResult<Map<String, String>> getConfigMap() {
        return ApiResult.success(settingService.getConfigMap());
    }

    /**
     * 查询设置项详情。
     *
     * @param id 设置项编号
     * @return 设置项详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "查询设置项详情")
    public ApiResult<SettingVO> getSetting(@PathVariable Long id) {
        return ApiResult.success(settingService.getSetting(id));
    }

    /**
     * 新增设置项。
     *
     * @param request 新增请求
     * @return 新设置项编号
     */
    @PostMapping
    @Operation(summary = "新增设置项")
    public ApiResult<Long> createSetting(@Valid @RequestBody SettingCreateRequest request) {
        return ApiResult.success(settingService.createSetting(request));
    }

    /**
     * 修改设置项。
     *
     * @param id      设置项编号
     * @param request 修改请求
     * @return 操作结果
     */
    @PutMapping("/{id}")
    @Operation(summary = "修改设置项", description = "配置键不允许修改")
    public ApiResult<Void> updateSetting(@PathVariable Long id,
                                         @Valid @RequestBody SettingUpdateRequest request) {
        settingService.updateSetting(id, request);
        return ApiResult.success();
    }

    /**
     * 删除设置项。
     *
     * @param id 设置项编号
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除设置项")
    public ApiResult<Void> deleteSetting(@PathVariable Long id) {
        settingService.deleteSetting(id);
        return ApiResult.success();
    }

    /**
     * 按分组批量保存设置项的值。
     *
     * @param request 批量保存请求
     * @return 实际更新的条数
     */
    @PutMapping("/batch")
    @Operation(summary = "按分组批量保存", description = "批量更新指定配置键的值，配置键需已存在且归属正确分组")
    public ApiResult<Integer> batchSave(@Valid @RequestBody SettingBatchSaveRequest request) {
        return ApiResult.success(settingService.batchSave(request));
    }
}
