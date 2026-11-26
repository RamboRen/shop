package com.shop.admin.product.controller;

import com.shop.admin.common.api.ApiResult;
import com.shop.admin.common.api.PageResult;
import com.shop.admin.product.dto.ProductBatchStatusRequest;
import com.shop.admin.product.dto.ProductCreateRequest;
import com.shop.admin.product.dto.ProductQueryRequest;
import com.shop.admin.product.dto.ProductStatusRequest;
import com.shop.admin.product.dto.ProductUpdateRequest;
import com.shop.admin.product.service.ProductService;
import com.shop.admin.product.vo.ProductVO;
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
 * 商品管理接口。
 *
 * @author shop-admin
 */
@RestController
@RequestMapping("/api/admin/products")
@RequiredArgsConstructor
@PreAuthorize("hasRole('SUPER_ADMIN') or hasAuthority('product:view')")
@Tag(name = "商品管理", description = "商品的查询、新增、修改、删除与上下架")
@SecurityRequirement(name = "bearerAuth")
public class ProductController {

    private final ProductService productService;

    /**
     * 分页查询商品。
     *
     * @param request 查询条件
     * @return 商品分页结果
     */
    @GetMapping
    @Operation(summary = "分页查询商品")
    public ApiResult<PageResult<ProductVO>> pageProducts(@Valid @ParameterObject ProductQueryRequest request) {
        return ApiResult.success(productService.pageProducts(request));
    }

    /**
     * 查询商品详情。
     *
     * @param id 商品编号
     * @return 商品信息
     */
    @GetMapping("/{id}")
    @Operation(summary = "查询商品详情")
    public ApiResult<ProductVO> getProduct(@PathVariable Long id) {
        return ApiResult.success(productService.getProduct(id));
    }

    /**
     * 新增商品。
     *
     * @param request 新增请求
     * @return 新商品编号
     */
    @PostMapping
    @Operation(summary = "新增商品")
    public ApiResult<Long> createProduct(@Valid @RequestBody ProductCreateRequest request) {
        return ApiResult.success(productService.createProduct(request));
    }

    /**
     * 修改商品。
     *
     * @param id      商品编号
     * @param request 修改请求
     * @return 操作结果
     */
    @PutMapping("/{id}")
    @Operation(summary = "修改商品")
    public ApiResult<Void> updateProduct(@PathVariable Long id,
                                         @Valid @RequestBody ProductUpdateRequest request) {
        productService.updateProduct(id, request);
        return ApiResult.success();
    }

    /**
     * 删除商品。
     *
     * @param id 商品编号
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除商品")
    public ApiResult<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ApiResult.success();
    }

    /**
     * 修改单个商品上下架状态。
     *
     * @param id      商品编号
     * @param request 状态请求
     * @return 操作结果
     */
    @PutMapping("/{id}/status")
    @Operation(summary = "修改商品上下架状态")
    public ApiResult<Void> updateStatus(@PathVariable Long id,
                                        @Valid @RequestBody ProductStatusRequest request) {
        productService.updateStatus(id, request);
        return ApiResult.success();
    }

    /**
     * 批量修改商品上下架状态。
     *
     * @param request 批量状态请求
     * @return 操作结果
     */
    @PutMapping("/batch-status")
    @Operation(summary = "批量修改商品上下架状态")
    public ApiResult<Void> batchUpdateStatus(@Valid @RequestBody ProductBatchStatusRequest request) {
        productService.batchUpdateStatus(request);
        return ApiResult.success();
    }
}
