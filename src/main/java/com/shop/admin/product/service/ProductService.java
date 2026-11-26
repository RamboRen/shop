package com.shop.admin.product.service;

import com.shop.admin.common.api.PageResult;
import com.shop.admin.product.dto.ProductBatchStatusRequest;
import com.shop.admin.product.dto.ProductCreateRequest;
import com.shop.admin.product.dto.ProductQueryRequest;
import com.shop.admin.product.dto.ProductStatusRequest;
import com.shop.admin.product.dto.ProductUpdateRequest;
import com.shop.admin.product.vo.ProductVO;

/**
 * 商品服务接口。
 *
 * @author shop-admin
 */
public interface ProductService {

    /**
     * 分页查询商品。
     *
     * @param request 查询条件
     * @return 商品分页结果
     */
    PageResult<ProductVO> pageProducts(ProductQueryRequest request);

    /**
     * 查询商品详情。
     *
     * @param id 商品编号
     * @return 商品信息
     */
    ProductVO getProduct(Long id);

    /**
     * 新增商品。
     *
     * @param request 新增请求
     * @return 新商品编号
     */
    Long createProduct(ProductCreateRequest request);

    /**
     * 修改商品。
     *
     * @param id      商品编号
     * @param request 修改请求
     */
    void updateProduct(Long id, ProductUpdateRequest request);

    /**
     * 删除商品。
     *
     * @param id 商品编号
     */
    void deleteProduct(Long id);

    /**
     * 修改单个商品上下架状态。
     *
     * @param id      商品编号
     * @param request 状态请求
     */
    void updateStatus(Long id, ProductStatusRequest request);

    /**
     * 批量修改商品上下架状态。
     *
     * @param request 批量状态请求
     */
    void batchUpdateStatus(ProductBatchStatusRequest request);
}
