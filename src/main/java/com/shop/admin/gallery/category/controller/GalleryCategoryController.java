package com.shop.admin.gallery.category.controller;

import com.shop.admin.common.api.ApiResult;
import com.shop.admin.gallery.category.dto.GalleryCategoryCreateRequest;
import com.shop.admin.gallery.category.dto.GalleryCategoryUpdateRequest;
import com.shop.admin.gallery.category.service.GalleryCategoryService;
import com.shop.admin.gallery.category.vo.GalleryCategoryVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 图库分类管理接口。
 *
 * @author shop-admin
 */
@RestController
@RequestMapping("/api/admin/gallery/categories")
@RequiredArgsConstructor
@PreAuthorize("hasRole('SUPER_ADMIN') or hasAuthority('gallery:view')")
@Tag(name = "图库分类", description = "图库分类的查询与维护")
@SecurityRequirement(name = "bearerAuth")
public class GalleryCategoryController {

    private final GalleryCategoryService galleryCategoryService;

    /**
     * 查询图库分类列表。
     *
     * @param status 状态筛选，为空时查询全部
     * @return 分类列表
     */
    @GetMapping
    @Operation(summary = "查询图库分类列表")
    public ApiResult<List<GalleryCategoryVO>> listCategories(
            @Parameter(description = "状态，0 禁用，1 启用")
            @RequestParam(required = false) Integer status) {
        return ApiResult.success(galleryCategoryService.listCategories(status));
    }

    /**
     * 查询分类详情。
     *
     * @param id 分类编号
     * @return 分类信息
     */
    @GetMapping("/{id}")
    @Operation(summary = "查询图库分类详情")
    public ApiResult<GalleryCategoryVO> getCategory(@PathVariable Long id) {
        return ApiResult.success(galleryCategoryService.getCategory(id));
    }

    /**
     * 新增图库分类。
     *
     * @param request 新增请求
     * @return 新分类编号
     */
    @PostMapping
    @Operation(summary = "新增图库分类")
    public ApiResult<Long> createCategory(@Valid @RequestBody GalleryCategoryCreateRequest request) {
        return ApiResult.success(galleryCategoryService.createCategory(request));
    }

    /**
     * 修改图库分类。
     *
     * @param id      分类编号
     * @param request 修改请求
     * @return 操作结果
     */
    @PutMapping("/{id}")
    @Operation(summary = "修改图库分类")
    public ApiResult<Void> updateCategory(@PathVariable Long id,
                                          @Valid @RequestBody GalleryCategoryUpdateRequest request) {
        galleryCategoryService.updateCategory(id, request);
        return ApiResult.success();
    }

    /**
     * 删除图库分类。
     *
     * @param id 分类编号
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除图库分类")
    public ApiResult<Void> deleteCategory(@PathVariable Long id) {
        galleryCategoryService.deleteCategory(id);
        return ApiResult.success();
    }
}
