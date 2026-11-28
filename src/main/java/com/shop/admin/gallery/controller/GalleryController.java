package com.shop.admin.gallery.controller;

import com.shop.admin.common.api.ApiResult;
import com.shop.admin.common.api.PageResult;
import com.shop.admin.gallery.dto.GalleryBatchDeleteRequest;
import com.shop.admin.gallery.dto.GalleryImageRenameRequest;
import com.shop.admin.gallery.dto.GalleryQueryRequest;
import com.shop.admin.gallery.service.GalleryService;
import com.shop.admin.gallery.vo.GalleryImageVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 图库管理接口。
 *
 * @author shop-admin
 */
@RestController
@RequestMapping("/api/admin/gallery")
@RequiredArgsConstructor
@PreAuthorize("hasRole('SUPER_ADMIN') or hasAuthority('gallery:view')")
@Tag(name = "图库管理", description = "图片上传、分页查询与删除")
@SecurityRequirement(name = "bearerAuth")
public class GalleryController {

    private final GalleryService galleryService;

    /**
     * 上传图片。
     *
     * @param file         图片文件
     * @param categoryCode 图库分类编码
     * @return 图片信息
     */
    @PostMapping(value = "/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "上传图片", description = "支持 JPG、PNG、GIF，单文件最大 10MB；分类编码须为已启用的分类，不传默认为 OTHER")
    public ApiResult<GalleryImageVO> uploadImage(
            @Parameter(description = "图片文件", required = true)
            @RequestPart("file") MultipartFile file,
            @Parameter(description = "图库分类编码，须为已启用的分类编码；不传时默认 OTHER")
            @RequestParam(required = false) String categoryCode) {
        return ApiResult.success(galleryService.uploadImage(file, categoryCode));
    }

    /**
     * 分页查询图库图片。
     *
     * @param request 查询条件
     * @return 图片分页结果
     */
    @GetMapping("/images")
    @Operation(summary = "分页查询图库图片")
    public ApiResult<PageResult<GalleryImageVO>> pageImages(
            @Valid @ParameterObject GalleryQueryRequest request) {
        return ApiResult.success(galleryService.pageImages(request));
    }

    /**
     * 按分类分页查询图片，支持查询已禁用分类下的历史图片。
     *
     * @param categoryCode 分类编码
     * @param request      分页与关键字查询条件
     * @return 图片分页结果
     */
    @GetMapping("/images/category/{categoryCode}")
    @Operation(summary = "按分类查询图片", description = "支持已禁用分类下的历史图片；查询条件中的 imageGroup 参数会被忽略")
    public ApiResult<PageResult<GalleryImageVO>> pageImagesByCategory(
            @Parameter(description = "图库分类编码", required = true)
            @PathVariable String categoryCode,
            @Valid @ParameterObject GalleryQueryRequest request) {
        return ApiResult.success(galleryService.pageImagesByCategory(categoryCode, request));
    }

    /**
     * 修改图片名称。
     *
     * @param id      图片编号
     * @param request 修改请求
     * @return 修改后的图片信息
     */
    @PutMapping("/images/{id}/name")
    @Operation(summary = "修改图片名称",
            description = "仅修改展示名称 originalName，不改变存储文件名与访问地址")
    public ApiResult<GalleryImageVO> renameImage(@PathVariable Long id,
                                                 @Valid @RequestBody GalleryImageRenameRequest request) {
        return ApiResult.success(galleryService.renameImage(id, request));
    }

    /**
     * 删除图片。
     *
     * @param id 图片编号
     * @return 操作结果
     */
    @DeleteMapping("/images/{id}")
    @Operation(summary = "删除图片")
    public ApiResult<Void> deleteImage(@PathVariable Long id) {
        galleryService.deleteImage(id);
        return ApiResult.success();
    }

    /**
     * 批量删除图片。
     *
     * @param request 批量删除请求
     * @return 操作结果
     */
    @DeleteMapping("/images/batch")
    @Operation(summary = "批量删除图片")
    public ApiResult<Void> batchDeleteImages(@Valid @RequestBody GalleryBatchDeleteRequest request) {
        galleryService.batchDeleteImages(request.getIds());
        return ApiResult.success();
    }
}
