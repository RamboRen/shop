package com.shop.admin.spec.service;

import com.shop.admin.common.api.PageResult;
import com.shop.admin.spec.dto.SpecCreateRequest;
import com.shop.admin.spec.dto.SpecQueryRequest;
import com.shop.admin.spec.dto.SpecStatusRequest;
import com.shop.admin.spec.dto.SpecUpdateRequest;
import com.shop.admin.spec.vo.ProductSpecVO;

/**
 * 商品规格服务接口。
 *
 * @author shop-admin
 */
public interface ProductSpecService {

    /**
     * 分页查询商品规格。
     *
     * @param request 查询条件
     * @return 规格分页结果
     */
    PageResult<ProductSpecVO> pageSpecs(SpecQueryRequest request);

    /**
     * 查询商品规格详情。
     *
     * @param id 规格编号
     * @return 规格信息
     */
    ProductSpecVO getSpec(Long id);

    /**
     * 新增商品规格。
     *
     * @param request 新增请求
     * @return 新规格编号
     */
    Long createSpec(SpecCreateRequest request);

    /**
     * 修改商品规格。
     *
     * @param id      规格编号
     * @param request 修改请求
     */
    void updateSpec(Long id, SpecUpdateRequest request);

    /**
     * 删除商品规格。
     *
     * @param id 规格编号
     */
    void deleteSpec(Long id);

    /**
     * 修改商品规格状态。
     *
     * @param id      规格编号
     * @param request 状态请求
     */
    void updateStatus(Long id, SpecStatusRequest request);
}
