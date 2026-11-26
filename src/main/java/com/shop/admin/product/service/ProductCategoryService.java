package com.shop.admin.product.service;

import com.shop.admin.product.dto.CategoryCreateRequest;
import com.shop.admin.product.dto.CategoryUpdateRequest;
import com.shop.admin.product.entity.ProductCategoryDO;
import com.shop.admin.product.vo.ProductCategoryVO;

import java.util.List;

/**
 * 商品分类服务接口。
 *
 * @author shop-admin
 */
public interface ProductCategoryService {

    /**
     * 查询分类列表。
     *
     * @param status 状态筛选，为空时查询全部
     * @return 分类列表
     */
    List<ProductCategoryVO> listCategories(Integer status);

    /**
     * 查询分类详情。
     *
     * @param id 分类编号
     * @return 分类信息
     */
    ProductCategoryVO getCategory(Long id);

    /**
     * 新增分类。
     *
     * @param request 新增请求
     * @return 新分类编号
     */
    Long createCategory(CategoryCreateRequest request);

    /**
     * 修改分类。
     *
     * @param id      分类编号
     * @param request 修改请求
     */
    void updateCategory(Long id, CategoryUpdateRequest request);

    /**
     * 删除分类。
     *
     * @param id 分类编号
     */
    void deleteCategory(Long id);

    /**
     * 获取指定分类，不存在时抛出业务异常。
     *
     * @param id 分类编号
     * @return 分类数据对象
     */
    ProductCategoryDO getRequiredCategory(Long id);

    /**
     * 获取启用的指定分类，不存在或已禁用时抛出业务异常。
     *
     * @param id 分类编号
     * @return 分类数据对象
     */
    ProductCategoryDO getRequiredEnabledCategory(Long id);
}
