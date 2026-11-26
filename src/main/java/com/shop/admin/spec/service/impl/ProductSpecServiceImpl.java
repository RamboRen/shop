package com.shop.admin.spec.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shop.admin.common.api.PageResult;
import com.shop.admin.common.constant.CommonConstants;
import com.shop.admin.common.enums.CommonStatus;
import com.shop.admin.common.exception.BusinessException;
import com.shop.admin.spec.dto.SpecCreateRequest;
import com.shop.admin.spec.dto.SpecQueryRequest;
import com.shop.admin.spec.dto.SpecStatusRequest;
import com.shop.admin.spec.dto.SpecUpdateRequest;
import com.shop.admin.spec.entity.ProductSpecDO;
import com.shop.admin.spec.mapper.ProductSpecMapper;
import com.shop.admin.spec.service.ProductSpecService;
import com.shop.admin.spec.vo.ProductSpecVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * 商品规格服务实现。
 *
 * @author shop-admin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductSpecServiceImpl implements ProductSpecService {

    private static final int MAX_SPEC_VALUE_COUNT = 20;

    private final ProductSpecMapper productSpecMapper;

    @Override
    public PageResult<ProductSpecVO> pageSpecs(SpecQueryRequest request) {
        validateStatus(request.getStatus());
        int pageNum = request.getPageNum() == null ? CommonConstants.DEFAULT_PAGE_NUM : request.getPageNum();
        int pageSize = request.getPageSize() == null ? CommonConstants.DEFAULT_PAGE_SIZE : request.getPageSize();
        LambdaQueryWrapper<ProductSpecDO> wrapper = Wrappers.<ProductSpecDO>lambdaQuery();
        String keyword = trimToNull(request.getKeyword());
        if (keyword != null) {
            wrapper.like(ProductSpecDO::getName, keyword);
        }
        if (request.getStatus() != null) {
            wrapper.eq(ProductSpecDO::getStatus, request.getStatus());
        }
        wrapper.orderByAsc(ProductSpecDO::getSort).orderByDesc(ProductSpecDO::getCreateTime);
        Page<ProductSpecDO> specPage = productSpecMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        List<ProductSpecVO> records = specPage.getRecords().stream()
                .map(ProductSpecVO::from)
                .toList();
        return PageResult.of(specPage.getTotal(), specPage.getCurrent(), specPage.getSize(), records);
    }

    @Override
    public ProductSpecVO getSpec(Long id) {
        return ProductSpecVO.from(getRequiredSpec(id));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createSpec(SpecCreateRequest request) {
        String name = request.getName().trim();
        validateNameUnique(name, null);
        validateStatus(request.getStatus());
        ProductSpecDO specDO = new ProductSpecDO();
        specDO.setName(name);
        specDO.setValues(normalizeValues(request.getValues()));
        specDO.setStatus(request.getStatus() == null
                ? CommonStatus.ENABLED.getCode() : request.getStatus());
        specDO.setSort(request.getSort() == null ? 0 : request.getSort());
        specDO.setRemark(trimToNull(request.getRemark()));
        if (productSpecMapper.insert(specDO) != 1) {
            throw new BusinessException(500, "新增商品规格失败");
        }
        log.info("新增商品规格成功，规格编号: {}", specDO.getId());
        return specDO.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSpec(Long id, SpecUpdateRequest request) {
        getRequiredSpec(id);
        String name = request.getName().trim();
        validateNameUnique(name, id);
        validateStatus(request.getStatus());
        ProductSpecDO specDO = new ProductSpecDO();
        specDO.setId(id);
        specDO.setName(name);
        specDO.setValues(normalizeValues(request.getValues()));
        specDO.setStatus(request.getStatus());
        specDO.setSort(request.getSort() == null ? 0 : request.getSort());
        specDO.setRemark(trimToNull(request.getRemark()));
        if (productSpecMapper.updateById(specDO) != 1) {
            throw new BusinessException(500, "修改商品规格失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteSpec(Long id) {
        getRequiredSpec(id);
        if (productSpecMapper.deleteById(id) != 1) {
            throw new BusinessException(500, "删除商品规格失败");
        }
        log.info("删除商品规格成功，规格编号: {}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long id, SpecStatusRequest request) {
        getRequiredSpec(id);
        validateStatus(request.getStatus());
        ProductSpecDO specDO = new ProductSpecDO();
        specDO.setId(id);
        specDO.setStatus(request.getStatus());
        if (productSpecMapper.updateById(specDO) != 1) {
            throw new BusinessException(500, "修改商品规格状态失败");
        }
    }

    private ProductSpecDO getRequiredSpec(Long id) {
        ProductSpecDO specDO = productSpecMapper.selectById(id);
        if (specDO == null) {
            throw new BusinessException(404, "商品规格不存在");
        }
        return specDO;
    }

    private void validateNameUnique(String name, Long excludeId) {
        LambdaQueryWrapper<ProductSpecDO> wrapper = Wrappers.<ProductSpecDO>lambdaQuery()
                .eq(ProductSpecDO::getName, name);
        if (excludeId != null) {
            wrapper.ne(ProductSpecDO::getId, excludeId);
        }
        if (productSpecMapper.selectCount(wrapper) > 0) {
            throw new BusinessException(409, "规格名称已存在");
        }
    }

    private List<String> normalizeValues(List<String> values) {
        if (values == null || values.isEmpty()) {
            throw new BusinessException(400, "规格值不能为空");
        }
        Set<String> distinctValues = new LinkedHashSet<>();
        for (String value : values) {
            String normalized = trimToNull(value);
            if (normalized == null) {
                throw new BusinessException(400, "规格值不能为空");
            }
            if (!distinctValues.add(normalized)) {
                throw new BusinessException(400, "规格值不能重复");
            }
        }
        if (distinctValues.size() > MAX_SPEC_VALUE_COUNT) {
            throw new BusinessException(400, "规格值最多 20 个");
        }
        return new ArrayList<>(distinctValues);
    }

    private void validateStatus(Integer status) {
        if (status == null) {
            return;
        }
        try {
            CommonStatus.fromCode(status);
        } catch (IllegalArgumentException exception) {
            throw new BusinessException(400, "规格状态只能是 0 或 1");
        }
    }

    private String trimToNull(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }
}
