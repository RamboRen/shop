package com.shop.admin.setting.service;

import com.shop.admin.common.api.PageResult;
import com.shop.admin.setting.dto.SettingBatchSaveRequest;
import com.shop.admin.setting.dto.SettingCreateRequest;
import com.shop.admin.setting.dto.SettingQueryRequest;
import com.shop.admin.setting.dto.SettingUpdateRequest;
import com.shop.admin.setting.vo.SettingGroupVO;
import com.shop.admin.setting.vo.SettingVO;

import java.util.List;
import java.util.Map;

/**
 * 系统设置服务接口。
 *
 * @author shop-admin
 */
public interface SettingService {

    /**
     * 分页查询设置项。
     *
     * @param request 查询条件
     * @return 设置项分页结果
     */
    PageResult<SettingVO> pageSettings(SettingQueryRequest request);

    /**
     * 按分组查询全部设置项。
     *
     * @return 分组列表，每个分组包含其配置项
     */
    List<SettingGroupVO> listGroups();

    /**
     * 查询配置键值映射，供其他模块读取配置。
     *
     * @return 配置键到配置值的映射
     */
    Map<String, String> getConfigMap();

    /**
     * 查询设置项详情。
     *
     * @param id 设置项编号
     * @return 设置项详情
     */
    SettingVO getSetting(Long id);

    /**
     * 新增设置项。
     *
     * @param request 新增请求
     * @return 新设置项编号
     */
    Long createSetting(SettingCreateRequest request);

    /**
     * 修改设置项。
     *
     * @param id      设置项编号
     * @param request 修改请求
     */
    void updateSetting(Long id, SettingUpdateRequest request);

    /**
     * 删除设置项。
     *
     * @param id 设置项编号
     */
    void deleteSetting(Long id);

    /**
     * 按分组批量保存设置项的值。
     *
     * @param request 批量保存请求
     * @return 实际更新的条数
     */
    int batchSave(SettingBatchSaveRequest request);
}
