package com.shop.admin.setting.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shop.admin.common.api.PageResult;
import com.shop.admin.common.enums.SettingGroup;
import com.shop.admin.common.enums.SettingValueType;
import com.shop.admin.common.exception.BusinessException;
import com.shop.admin.security.SecurityUtils;
import com.shop.admin.setting.dto.SettingBatchSaveRequest;
import com.shop.admin.setting.dto.SettingCreateRequest;
import com.shop.admin.setting.dto.SettingQueryRequest;
import com.shop.admin.setting.dto.SettingUpdateRequest;
import com.shop.admin.setting.dto.SettingValueItem;
import com.shop.admin.setting.entity.SettingDO;
import com.shop.admin.setting.mapper.SettingMapper;
import com.shop.admin.setting.service.SettingService;
import com.shop.admin.setting.vo.SettingGroupVO;
import com.shop.admin.setting.vo.SettingVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 系统设置服务实现。
 *
 * @author shop-admin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SettingServiceImpl implements SettingService {

    private final SettingMapper settingMapper;

    @Override
    public PageResult<SettingVO> pageSettings(SettingQueryRequest request) {
        int pageNum = request.getPageNum() == null ? 1 : request.getPageNum();
        int pageSize = request.getPageSize() == null ? 10 : request.getPageSize();
        validateGroup(request.getConfigGroup());

        var wrapper = Wrappers.<SettingDO>lambdaQuery();
        String keyword = trimToNull(request.getKeyword());
        if (keyword != null) {
            wrapper.and(query -> query.like(SettingDO::getConfigKey, keyword)
                    .or()
                    .like(SettingDO::getConfigName, keyword));
        }
        if (StringUtils.hasText(request.getConfigGroup())) {
            wrapper.eq(SettingDO::getConfigGroup, request.getConfigGroup());
        }
        wrapper.orderByAsc(SettingDO::getConfigGroup).orderByAsc(SettingDO::getSort).orderByAsc(SettingDO::getId);

        Page<SettingDO> page = settingMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        List<SettingVO> records = page.getRecords().stream().map(SettingVO::from).toList();
        return PageResult.of(page.getTotal(), page.getCurrent(), page.getSize(), records);
    }

    @Override
    public List<SettingGroupVO> listGroups() {
        List<SettingDO> all = settingMapper.selectList(Wrappers.<SettingDO>lambdaQuery()
                .orderByAsc(SettingDO::getConfigGroup)
                .orderByAsc(SettingDO::getSort)
                .orderByAsc(SettingDO::getId));
        Map<String, List<SettingVO>> grouped = new LinkedHashMap<>();
        for (SettingDO settingDO : all) {
            grouped.computeIfAbsent(settingDO.getConfigGroup(), key -> new ArrayList<>())
                    .add(SettingVO.from(settingDO));
        }
        List<SettingGroupVO> result = new ArrayList<>();
        for (SettingGroup group : SettingGroup.values()) {
            result.add(SettingGroupVO.of(group, grouped.getOrDefault(group.getCode(), List.of())));
        }
        return result;
    }

    @Override
    public Map<String, String> getConfigMap() {
        List<SettingDO> all = settingMapper.selectList(Wrappers.<SettingDO>lambdaQuery());
        Map<String, String> map = new LinkedHashMap<>();
        for (SettingDO settingDO : all) {
            map.put(settingDO.getConfigKey(), settingDO.getConfigValue());
        }
        return map;
    }

    @Override
    public SettingVO getSetting(Long id) {
        return SettingVO.from(getRequiredSetting(id));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createSetting(SettingCreateRequest request) {
        String configKey = request.getConfigKey().trim();
        validateGroup(request.getConfigGroup());
        validateValueType(request.getValueType());
        if (settingMapper.selectCount(Wrappers.<SettingDO>lambdaQuery()
                .eq(SettingDO::getConfigKey, configKey)) > 0) {
            throw new BusinessException(409, "配置键已存在");
        }

        SettingDO settingDO = new SettingDO();
        settingDO.setConfigKey(configKey);
        settingDO.setConfigName(request.getConfigName().trim());
        settingDO.setConfigGroup(request.getConfigGroup());
        settingDO.setConfigValue(request.getConfigValue());
        settingDO.setValueType(request.getValueType());
        settingDO.setSort(request.getSort() == null ? 0 : request.getSort());
        settingDO.setRemark(trimToNull(request.getRemark()));
        if (settingMapper.insert(settingDO) != 1) {
            throw new BusinessException(500, "新增设置项失败");
        }
        log.info("新增设置项成功，配置键: {}, 操作人: {}", configKey, SecurityUtils.getCurrentAdminId());
        return settingDO.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSetting(Long id, SettingUpdateRequest request) {
        SettingDO existing = getRequiredSetting(id);
        validateValueType(request.getValueType());

        SettingDO updateDO = new SettingDO();
        updateDO.setId(id);
        updateDO.setConfigName(request.getConfigName().trim());
        updateDO.setConfigValue(request.getConfigValue());
        updateDO.setValueType(request.getValueType());
        updateDO.setSort(request.getSort() == null ? 0 : request.getSort());
        updateDO.setRemark(trimToNull(request.getRemark()));
        if (settingMapper.updateById(updateDO) != 1) {
            throw new BusinessException(500, "修改设置项失败");
        }
        log.info("修改设置项成功，配置键: {}, 操作人: {}", existing.getConfigKey(), SecurityUtils.getCurrentAdminId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteSetting(Long id) {
        SettingDO existing = getRequiredSetting(id);
        if (settingMapper.deleteById(id) != 1) {
            throw new BusinessException(500, "删除设置项失败");
        }
        log.info("删除设置项成功，配置键: {}, 操作人: {}", existing.getConfigKey(), SecurityUtils.getCurrentAdminId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchSave(SettingBatchSaveRequest request) {
        if (StringUtils.hasText(request.getConfigGroup())) {
            validateGroup(request.getConfigGroup());
        }
        // 同一配置键在后出现的覆盖先出现的
        Map<String, String> values = new LinkedHashMap<>();
        for (SettingValueItem item : request.getItems()) {
            values.put(item.getConfigKey().trim(), item.getConfigValue());
        }

        int updated = 0;
        for (Map.Entry<String, String> entry : values.entrySet()) {
            SettingDO existing = settingMapper.selectOne(Wrappers.<SettingDO>lambdaQuery()
                    .eq(SettingDO::getConfigKey, entry.getKey()));
            if (existing == null) {
                throw new BusinessException(404, "配置项不存在: " + entry.getKey());
            }
            if (StringUtils.hasText(request.getConfigGroup())
                    && !request.getConfigGroup().equals(existing.getConfigGroup())) {
                throw new BusinessException(400, "配置项 " + entry.getKey() + " 不属于分组 " + request.getConfigGroup());
            }
            SettingDO updateDO = new SettingDO();
            updateDO.setId(existing.getId());
            updateDO.setConfigValue(entry.getValue());
            if (settingMapper.updateById(updateDO) != 1) {
                throw new BusinessException(500, "保存设置项失败: " + entry.getKey());
            }
            updated++;
        }
        log.info("批量保存设置项成功，更新条数: {}, 操作人: {}", updated, SecurityUtils.getCurrentAdminId());
        return updated;
    }

    private SettingDO getRequiredSetting(Long id) {
        SettingDO settingDO = settingMapper.selectById(id);
        if (settingDO == null) {
            throw new BusinessException(404, "设置项不存在");
        }
        return settingDO;
    }

    private void validateGroup(String group) {
        if (StringUtils.hasText(group)) {
            try {
                SettingGroup.fromCode(group);
            } catch (IllegalArgumentException exception) {
                throw new BusinessException(400, "分组只能是 BASIC、CONTACT、TRADE 或 PAYMENT");
            }
        }
    }

    private void validateValueType(String valueType) {
        try {
            SettingValueType.fromCode(valueType);
        } catch (IllegalArgumentException exception) {
            throw new BusinessException(400, "值类型只能是 STRING、TEXT、NUMBER 或 BOOLEAN");
        }
    }

    private String trimToNull(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }
}
