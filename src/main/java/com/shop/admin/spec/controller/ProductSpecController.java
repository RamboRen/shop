package com.shop.admin.spec.controller;

import com.shop.admin.common.api.ApiResult;
import com.shop.admin.common.api.PageResult;
import com.shop.admin.spec.dto.SpecCreateRequest;
import com.shop.admin.spec.dto.SpecQueryRequest;
import com.shop.admin.spec.dto.SpecStatusRequest;
import com.shop.admin.spec.dto.SpecUpdateRequest;
import com.shop.admin.spec.service.ProductSpecService;
import com.shop.admin.spec.vo.ProductSpecVO;
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
 * 商品规格管理接口。
 *
 * @author shop-admin
 */
@RestController
@RequestMapping("/api/admin/product-specs")
@RequiredArgsConstructor
@PreAuthorize("hasRole('SUPER_ADMIN') or hasAuthority('product:spec')")
@Tag(name = "商品规格", description = "商品规格名称与规格值的查询和维护")
@SecurityRequirement(name = "bearerAuth")
public class ProductSpecController {

    private final ProductSpecService productSpecService;

    /**
     * 分页查询商品规格。
     *
     * @param request 查询条件
     * @return 规格分页结果
     */
    @GetMapping
    @Operation(summary = "分页查询商品规格")
    public ApiResult<PageResult<ProductSpecVO>> pageSpecs(@Valid @ParameterObject SpecQueryRequest request) {
        return ApiResult.success(productSpecService.pageSpecs(request));
    }

    /**
     * 查询商品规格详情。
     *
     * @param id 规格编号
     * @return 规格信息
     */
    @GetMapping("/{id}")
    @Operation(summary = "查询商品规格详情")
    public ApiResult<ProductSpecVO> getSpec(@PathVariable Long id) {
        return ApiResult.success(productSpecService.getSpec(id));
    }

    /**
     * 新增商品规格。
     *
     * @param request 新增请求
     * @return 新规格编号
     */
    @PostMapping
    @Operation(summary = "新增商品规格")
    public ApiResult<Long> createSpec(@Valid @RequestBody SpecCreateRequest request) {
        return ApiResult.success(productSpecService.createSpec(request));
    }

    /**
     * 修改商品规格。
     *
     * @param id      规格编号
     * @param request 修改请求
     * @return 操作结果
     */
    @PutMapping("/{id}")
    @Operation(summary = "修改商品规格")
    public ApiResult<Void> updateSpec(@PathVariable Long id,
                                      @Valid @RequestBody SpecUpdateRequest request) {
        productSpecService.updateSpec(id, request);
        return ApiResult.success();
    }

    /**
     * 删除商品规格。
     *
     * @param id 规格编号
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除商品规格")
    public ApiResult<Void> deleteSpec(@PathVariable Long id) {
        productSpecService.deleteSpec(id);
        return ApiResult.success();
    }

    /**
     * 修改商品规格状态。
     *
     * @param id      规格编号
     * @param request 状态请求
     * @return 操作结果
     */
    @PutMapping("/{id}/status")
    @Operation(summary = "修改商品规格状态")
    public ApiResult<Void> updateStatus(@PathVariable Long id,
                                        @Valid @RequestBody SpecStatusRequest request) {
        productSpecService.updateStatus(id, request);
        return ApiResult.success();
    }
}
