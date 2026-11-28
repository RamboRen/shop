package com.shop.admin.gallery.service;

import com.shop.admin.common.api.PageResult;
import com.shop.admin.gallery.dto.GalleryQueryRequest;
import com.shop.admin.gallery.dto.GalleryImageRenameRequest;
import com.shop.admin.gallery.vo.GalleryImageVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 图库服务接口。
 *
 * @author shop-admin
 */
public interface GalleryService {

    /**
     * 上传图片并保存图库记录。
     *
     * @param file         上传文件
     * @param categoryCode 图库分类编码，为空时默认 OTHER，且必须为已启用的分类
     * @return 图片信息
     */
    GalleryImageVO uploadImage(MultipartFile file, String categoryCode);

    /**
     * 分页查询图库图片。
     *
     * @param request 查询条件
     * @return 图片分页结果
     */
    PageResult<GalleryImageVO> pageImages(GalleryQueryRequest request);

    /**
     * 按分类分页查询图片，支持查询已禁用分类下的历史图片。
     *
     * @param categoryCode 分类编码
     * @param request      分页与关键字条件，其中的 imageGroup 会被忽略
     * @return 图片分页结果
     */
    PageResult<GalleryImageVO> pageImagesByCategory(String categoryCode, GalleryQueryRequest request);

    /**
     * 修改图片名称，仅修改展示名称，不改动存储文件名与访问地址。
     *
     * @param id      图片编号
     * @param request 修改请求
     * @return 修改后的图片信息
     */
    GalleryImageVO renameImage(Long id, GalleryImageRenameRequest request);

    /**
     * 删除图片。
     *
     * @param id 图片编号
     */
    void deleteImage(Long id);

    /**
     * 批量删除图片。
     *
     * @param ids 图片编号列表
     */
    void batchDeleteImages(List<Long> ids);
}
