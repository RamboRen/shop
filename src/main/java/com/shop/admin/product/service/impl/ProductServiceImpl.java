package com.shop.admin.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shop.admin.common.api.PageResult;
import com.shop.admin.common.constant.CommonConstants;
import com.shop.admin.common.enums.ProductStatus;
import com.shop.admin.common.exception.BusinessException;
import com.shop.admin.product.dto.ProductBatchStatusRequest;
import com.shop.admin.product.dto.ProductCreateRequest;
import com.shop.admin.product.dto.ProductQueryRequest;
import com.shop.admin.product.dto.ProductStatusRequest;
import com.shop.admin.product.dto.ProductUpdateRequest;
import com.shop.admin.product.entity.ProductCategoryDO;
import com.shop.admin.product.entity.ProductDO;
import com.shop.admin.product.mapper.ProductCategoryMapper;
import com.shop.admin.product.mapper.ProductMapper;
import com.shop.admin.product.service.ProductCategoryService;
import com.shop.admin.product.service.ProductService;
import com.shop.admin.product.vo.ProductVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 商品服务实现。
 *
 * @author shop-admin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private static final String UNCATEGORIZED_NAME = "未分类";

    private final ProductMapper productMapper;

    private final ProductCategoryMapper productCategoryMapper;

    private final ProductCategoryService productCategoryService;

    @Override
    public PageResult<ProductVO> pageProducts(ProductQueryRequest request) {
        validatePriceRange(request.getMinPrice(), request.getMaxPrice());
        validateStatus(request.getStatus());
        int pageNum = request.getPageNum() == null ? CommonConstants.DEFAULT_PAGE_NUM : request.getPageNum();
        int pageSize = request.getPageSize() == null ? CommonConstants.DEFAULT_PAGE_SIZE : request.getPageSize();

        LambdaQueryWrapper<ProductDO> wrapper = Wrappers.<ProductDO>lambdaQuery();
        String keyword = trimToNull(request.getKeyword());
        if (keyword != null) {
            wrapper.and(query -> query.like(ProductDO::getName, keyword)
                    .or()
                    .like(ProductDO::getProductCode, keyword));
        }
        if (request.getCategoryId() != null) {
            wrapper.eq(ProductDO::getCategoryId, request.getCategoryId());
        }
        if (request.getStatus() != null) {
            wrapper.eq(ProductDO::getStatus, request.getStatus());
        }
        if (request.getMinPrice() != null) {
            wrapper.ge(ProductDO::getPrice, request.getMinPrice());
        }
        if (request.getMaxPrice() != null) {
            wrapper.le(ProductDO::getPrice, request.getMaxPrice());
        }
        wrapper.orderByDesc(ProductDO::getCreateTime);

        Page<ProductDO> productPage = productMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        Map<Long, String> categoryNameMap = loadCategoryNameMap(productPage.getRecords());
        List<ProductVO> records = productPage.getRecords().stream()
                .map(product -> ProductVO.from(product,
                        categoryNameMap.getOrDefault(product.getCategoryId(), UNCATEGORIZED_NAME)))
                .toList();
        return PageResult.of(productPage.getTotal(), productPage.getCurrent(), productPage.getSize(), records);
    }

    @Override
    public ProductVO getProduct(Long id) {
        ProductDO productDO = getRequiredProduct(id);
        ProductCategoryDO categoryDO = productCategoryMapper.selectById(productDO.getCategoryId());
        String categoryName = categoryDO == null ? UNCATEGORIZED_NAME : categoryDO.getName();
        return ProductVO.from(productDO, categoryName);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createProduct(ProductCreateRequest request) {
        productCategoryService.getRequiredEnabledCategory(request.getCategoryId());
        validateOriginalPrice(request.getPrice(), request.getOriginalPrice());
        validateStatus(request.getStatus());

        String productCode = trimToNull(request.getProductCode());
        if (productCode == null) {
            productCode = generateProductCode();
        }
        if (productMapper.selectCount(Wrappers.<ProductDO>lambdaQuery()
                .eq(ProductDO::getProductCode, productCode)) > 0) {
            throw new BusinessException(409, "商品编码已存在");
        }

        ProductDO productDO = new ProductDO();
        productDO.setProductCode(productCode);
        productDO.setCategoryId(request.getCategoryId());
        productDO.setName(request.getName().trim());
        productDO.setSubtitle(trimToNull(request.getSubtitle()));
        productDO.setMainImage(request.getMainImage().trim());
        productDO.setImageUrls(copyImageUrls(request.getImageUrls()));
        productDO.setPrice(request.getPrice());
        productDO.setOriginalPrice(request.getOriginalPrice());
        productDO.setStock(request.getStock());
        productDO.setSalesVolume(0L);
        productDO.setStatus(request.getStatus() == null
                ? ProductStatus.OFF_SHELF.getCode() : request.getStatus());
        productDO.setSort(request.getSort() == null ? 0 : request.getSort());
        productDO.setDetail(trimToNull(request.getDetail()));
        if (productMapper.insert(productDO) != 1) {
            throw new BusinessException(500, "新增商品失败");
        }
        log.info("新增商品成功，商品编号: {}", productDO.getId());
        return productDO.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateProduct(Long id, ProductUpdateRequest request) {
        getRequiredProduct(id);
        productCategoryService.getRequiredEnabledCategory(request.getCategoryId());
        validateOriginalPrice(request.getPrice(), request.getOriginalPrice());
        ProductDO productDO = new ProductDO();
        productDO.setId(id);
        productDO.setCategoryId(request.getCategoryId());
        productDO.setName(request.getName().trim());
        productDO.setSubtitle(trimToNull(request.getSubtitle()));
        productDO.setMainImage(request.getMainImage().trim());
        productDO.setImageUrls(request.getImageUrls() == null
                ? null : new ArrayList<>(request.getImageUrls()));
        productDO.setPrice(request.getPrice());
        productDO.setOriginalPrice(request.getOriginalPrice());
        productDO.setStock(request.getStock());
        productDO.setSort(request.getSort() == null ? 0 : request.getSort());
        productDO.setDetail(trimToNull(request.getDetail()));
        if (productMapper.updateById(productDO) != 1) {
            throw new BusinessException(500, "修改商品失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteProduct(Long id) {
        getRequiredProduct(id);
        if (productMapper.deleteById(id) != 1) {
            throw new BusinessException(500, "删除商品失败");
        }
        log.info("删除商品成功，商品编号: {}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long id, ProductStatusRequest request) {
        getRequiredProduct(id);
        validateStatus(request.getStatus());
        ProductDO productDO = new ProductDO();
        productDO.setId(id);
        productDO.setStatus(request.getStatus());
        if (productMapper.updateById(productDO) != 1) {
            throw new BusinessException(500, "修改商品状态失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchUpdateStatus(ProductBatchStatusRequest request) {
        validateStatus(request.getStatus());
        List<Long> distinctIds = request.getIds().stream().distinct().toList();
        if (distinctIds.size() != request.getIds().size()) {
            throw new BusinessException(400, "商品编号不能重复");
        }
        long existingCount = productMapper.selectCount(Wrappers.<ProductDO>lambdaQuery()
                .in(ProductDO::getId, distinctIds));
        if (existingCount != distinctIds.size()) {
            throw new BusinessException(404, "部分商品不存在或已被删除");
        }
        LambdaUpdateWrapper<ProductDO> wrapper = Wrappers.<ProductDO>lambdaUpdate()
                .in(ProductDO::getId, distinctIds)
                .set(ProductDO::getStatus, request.getStatus())
                .set(ProductDO::getUpdateTime, LocalDateTime.now());
        if (productMapper.update(null, wrapper) != distinctIds.size()) {
            throw new BusinessException(500, "批量修改商品状态失败");
        }
    }

    private ProductDO getRequiredProduct(Long id) {
        ProductDO productDO = productMapper.selectById(id);
        if (productDO == null) {
            throw new BusinessException(404, "商品不存在");
        }
        return productDO;
    }

    private Map<Long, String> loadCategoryNameMap(List<ProductDO> products) {
        Set<Long> categoryIds = new HashSet<>();
        for (ProductDO product : products) {
            if (product.getCategoryId() != null) {
                categoryIds.add(product.getCategoryId());
            }
        }
        if (categoryIds.isEmpty()) {
            return Map.of();
        }
        List<ProductCategoryDO> categories = productCategoryMapper.selectBatchIds(categoryIds);
        return categories.stream()
                .collect(Collectors.toMap(ProductCategoryDO::getId,
                        ProductCategoryDO::getName, (first, second) -> first));
    }

    private void validatePriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        if (minPrice != null && maxPrice != null && minPrice.compareTo(maxPrice) > 0) {
            throw new BusinessException(400, "最低价格不能大于最高价格");
        }
    }

    private void validateOriginalPrice(BigDecimal price, BigDecimal originalPrice) {
        if (originalPrice != null && originalPrice.compareTo(price) < 0) {
            throw new BusinessException(400, "划线原价不能低于销售价");
        }
    }

    private void validateStatus(Integer status) {
        if (status == null) {
            return;
        }
        try {
            ProductStatus.fromCode(status);
        } catch (IllegalArgumentException exception) {
            throw new BusinessException(400, "商品状态只能是 0 或 1");
        }
    }

    private List<String> copyImageUrls(List<String> imageUrls) {
        return imageUrls == null ? new ArrayList<>() : new ArrayList<>(imageUrls);
    }

    private String generateProductCode() {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        return "SP" + uuid.substring(0, 16).toUpperCase(Locale.ROOT);
    }

    private String trimToNull(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }
}
