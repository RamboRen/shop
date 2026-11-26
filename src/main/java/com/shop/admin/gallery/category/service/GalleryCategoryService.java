package com.shop.admin.gallery.category.service;

import com.shop.admin.gallery.category.dto.GalleryCategoryCreateRequest;
import com.shop.admin.gallery.category.dto.GalleryCategoryUpdateRequest;
import com.shop.admin.gallery.category.entity.GalleryCategoryDO;
import com.shop.admin.gallery.category.vo.GalleryCategoryVO;

import java.util.List;
import java.util.Map;

/**
 * 图库分类服务接口。
 *
 * @author shop-admin
 */
public interface GalleryCategoryService {

    /**
     * 查询分类列表，附带各分类下的图片数量。
     *
     * @param status 状态筛选，为空时查询全部
     * @return 分类列表
     */
    List<GalleryCategoryVO> listCategories(Integer status);

    /**
     * 查询分类详情。
     *
     * @param id 分类编号
     * @return 分类信息
     */
    GalleryCategoryVO getCategory(Long id);

    /**
     * 新增分类。
     *
     * @param request 新增请求
     * @return 新分类编号
     */
    Long createCategory(GalleryCategoryCreateRequest request);

    /**
     * 修改分类，分类编码不可修改。
     *
     * @param id      分类编号
     * @param request 修改请求
     */
    void updateCategory(Long id, GalleryCategoryUpdateRequest request);

    /**
     * 删除分类，分类下存在图片时禁止删除。
     *
     * @param id 分类编号
     */
    void deleteCategory(Long id);

    /**
     * 按编码查询启用的分类，供图片上传校验使用。
     *
     * @param code 分类编码
     * @return 启用分类，不存在或已禁用时抛出业务异常
     */
    GalleryCategoryDO getRequiredEnabledCategory(String code);

    /**
     * 查询所有启用分类的编码与名称映射，供图片列表展示分类名。
     *
     * @return 编码到名称的映射
     */
    Map<String, String> getEnabledCategoryNames();

    /**
     * 按编码查询分类，不校验启用状态，供按分类查询历史图片使用。
     *
     * @param code 分类编码
     * @return 分类信息，不存在时抛出业务异常
     */
    GalleryCategoryDO getCategoryByCode(String code);

    /**
     * 查询所有分类（含禁用）的编码与名称映射，供按分类查询时展示分类名。
     *
     * @return 编码到名称的映射
     */
    Map<String, String> getAllCategoryNames();
}
