package com.shop.admin.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.shop.admin.common.enums.CommonStatus;
import com.shop.admin.common.exception.BusinessException;
import com.shop.admin.product.dto.CategoryCreateRequest;
import com.shop.admin.product.dto.CategoryUpdateRequest;
import com.shop.admin.product.entity.ProductCategoryDO;
import com.shop.admin.product.entity.ProductDO;
import com.shop.admin.product.mapper.ProductCategoryMapper;
import com.shop.admin.product.mapper.ProductMapper;
import com.shop.admin.product.service.ProductCategoryService;
import com.shop.admin.product.vo.ProductCategoryVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 商品分类服务实现。
 *
 * @author shop-admin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductCategoryServiceImpl implements ProductCategoryService {

    private final ProductCategoryMapper productCategoryMapper;

    private final ProductMapper productMapper;

    @Override
    public List<ProductCategoryVO> listCategories(Integer status) {
        validateStatus(status);
        LambdaQueryWrapper<ProductCategoryDO> wrapper = Wrappers.<ProductCategoryDO>lambdaQuery()
                .eq(status != null, ProductCategoryDO::getStatus, status)
                .orderByAsc(ProductCategoryDO::getSort)
                .orderByDesc(ProductCategoryDO::getCreateTime);
        return productCategoryMapper.selectList(wrapper).stream()
                .map(ProductCategoryVO::from)
                .toList();
    }

    @Override
    public ProductCategoryVO getCategory(Long id) {
        return ProductCategoryVO.from(getRequiredCategory(id));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createCategory(CategoryCreateRequest request) {
        String name = request.getName().trim();
        if (productCategoryMapper.selectCount(Wrappers.<ProductCategoryDO>lambdaQuery()
                .eq(ProductCategoryDO::getName, name)) > 0) {
            throw new BusinessException(409, "分类名称已存在");
        }
        ProductCategoryDO categoryDO = new ProductCategoryDO();
        categoryDO.setName(name);
        categoryDO.setSort(request.getSort());
        categoryDO.setStatus(request.getStatus());
        if (productCategoryMapper.insert(categoryDO) != 1) {
            throw new BusinessException(500, "新增商品分类失败");
        }
        log.info("新增商品分类成功，分类编号: {}", categoryDO.getId());
        return categoryDO.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCategory(Long id, CategoryUpdateRequest request) {
        getRequiredCategory(id);
        String name = request.getName().trim();
        if (productCategoryMapper.selectCount(Wrappers.<ProductCategoryDO>lambdaQuery()
                .eq(ProductCategoryDO::getName, name)
                .ne(ProductCategoryDO::getId, id)) > 0) {
            throw new BusinessException(409, "分类名称已存在");
        }
        ProductCategoryDO categoryDO = new ProductCategoryDO();
        categoryDO.setId(id);
        categoryDO.setName(name);
        categoryDO.setSort(request.getSort());
        categoryDO.setStatus(request.getStatus());
        if (productCategoryMapper.updateById(categoryDO) != 1) {
            throw new BusinessException(500, "修改商品分类失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCategory(Long id) {
        getRequiredCategory(id);
        if (productMapper.selectCount(Wrappers.<ProductDO>lambdaQuery()
                .eq(ProductDO::getCategoryId, id)) > 0) {
            throw new BusinessException(400, "该分类下存在商品，不能删除");
        }
        if (productCategoryMapper.deleteById(id) != 1) {
            throw new BusinessException(500, "删除商品分类失败");
        }
        log.info("删除商品分类成功，分类编号: {}", id);
    }

    @Override
    public ProductCategoryDO getRequiredCategory(Long id) {
        ProductCategoryDO categoryDO = productCategoryMapper.selectById(id);
        if (categoryDO == null) {
            throw new BusinessException(404, "商品分类不存在");
        }
        return categoryDO;
    }

    @Override
    public ProductCategoryDO getRequiredEnabledCategory(Long id) {
        ProductCategoryDO categoryDO = getRequiredCategory(id);
        if (CommonStatus.DISABLED.getCode() == categoryDO.getStatus()) {
            throw new BusinessException(400, "商品分类已禁用");
        }
        return categoryDO;
    }

    private void validateStatus(Integer status) {
        if (status == null) {
            return;
        }
        try {
            CommonStatus.fromCode(status);
        } catch (IllegalArgumentException exception) {
            throw new BusinessException(400, "分类状态只能是 0 或 1");
        }
    }
}
