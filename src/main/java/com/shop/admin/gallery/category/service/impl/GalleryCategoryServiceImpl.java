package com.shop.admin.gallery.category.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.shop.admin.common.enums.CommonStatus;
import com.shop.admin.common.exception.BusinessException;
import com.shop.admin.gallery.category.dto.GalleryCategoryCreateRequest;
import com.shop.admin.gallery.category.dto.GalleryCategoryUpdateRequest;
import com.shop.admin.gallery.category.entity.GalleryCategoryDO;
import com.shop.admin.gallery.category.mapper.GalleryCategoryMapper;
import com.shop.admin.gallery.category.service.GalleryCategoryService;
import com.shop.admin.gallery.category.vo.GalleryCategoryVO;
import com.shop.admin.gallery.entity.GalleryImageDO;
import com.shop.admin.gallery.mapper.GalleryImageMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * 图库分类服务实现。
 *
 * @author shop-admin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GalleryCategoryServiceImpl implements GalleryCategoryService {

    private final GalleryCategoryMapper galleryCategoryMapper;

    private final GalleryImageMapper galleryImageMapper;

    @Override
    public List<GalleryCategoryVO> listCategories(Integer status) {
        validateStatus(status);
        LambdaQueryWrapper<GalleryCategoryDO> wrapper = Wrappers.<GalleryCategoryDO>lambdaQuery()
                .eq(status != null, GalleryCategoryDO::getStatus, status)
                .orderByAsc(GalleryCategoryDO::getSort)
                .orderByAsc(GalleryCategoryDO::getId);
        List<GalleryCategoryDO> categories = galleryCategoryMapper.selectList(wrapper);
        Map<String, Long> imageCountMap = countImagesByGroup();
        return categories.stream()
                .map(category -> GalleryCategoryVO.from(
                        category, imageCountMap.get(category.getCategoryCode())))
                .toList();
    }

    @Override
    public GalleryCategoryVO getCategory(Long id) {
        GalleryCategoryDO categoryDO = getRequiredCategory(id);
        Map<String, Long> imageCountMap = countImagesByGroup();
        return GalleryCategoryVO.from(categoryDO, imageCountMap.get(categoryDO.getCategoryCode()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createCategory(GalleryCategoryCreateRequest request) {
        String code = normalizeCode(request.getCategoryCode());
        String name = request.getCategoryName().trim();
        if (galleryCategoryMapper.selectCount(Wrappers.<GalleryCategoryDO>lambdaQuery()
                .eq(GalleryCategoryDO::getCategoryCode, code)) > 0) {
            throw new BusinessException(409, "分类编码已存在");
        }
        if (galleryCategoryMapper.selectCount(Wrappers.<GalleryCategoryDO>lambdaQuery()
                .eq(GalleryCategoryDO::getCategoryName, name)) > 0) {
            throw new BusinessException(409, "分类名称已存在");
        }
        GalleryCategoryDO categoryDO = new GalleryCategoryDO();
        categoryDO.setCategoryCode(code);
        categoryDO.setCategoryName(name);
        categoryDO.setSort(request.getSort());
        categoryDO.setStatus(request.getStatus());
        categoryDO.setRemark(trimToNull(request.getRemark()));
        if (galleryCategoryMapper.insert(categoryDO) != 1) {
            throw new BusinessException(500, "新增图库分类失败");
        }
        log.info("新增图库分类成功，分类编号: {}, 编码: {}", categoryDO.getId(), code);
        return categoryDO.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCategory(Long id, GalleryCategoryUpdateRequest request) {
        GalleryCategoryDO existing = getRequiredCategory(id);
        String name = request.getCategoryName().trim();
        if (galleryCategoryMapper.selectCount(Wrappers.<GalleryCategoryDO>lambdaQuery()
                .eq(GalleryCategoryDO::getCategoryName, name)
                .ne(GalleryCategoryDO::getId, id)) > 0) {
            throw new BusinessException(409, "分类名称已存在");
        }
        GalleryCategoryDO categoryDO = new GalleryCategoryDO();
        categoryDO.setId(id);
        categoryDO.setCategoryName(name);
        categoryDO.setSort(request.getSort());
        categoryDO.setStatus(request.getStatus());
        categoryDO.setRemark(trimToNull(request.getRemark()));
        if (galleryCategoryMapper.updateById(categoryDO) != 1) {
            throw new BusinessException(500, "修改图库分类失败");
        }
        log.info("修改图库分类成功，分类编号: {}, 编码: {}", id, existing.getCategoryCode());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCategory(Long id) {
        GalleryCategoryDO categoryDO = getRequiredCategory(id);
        long imageCount = galleryImageMapper.selectCount(Wrappers.<GalleryImageDO>lambdaQuery()
                .eq(GalleryImageDO::getImageGroup, categoryDO.getCategoryCode()));
        if (imageCount > 0) {
            throw new BusinessException(400, "该分类下存在图片，不能删除");
        }
        if (galleryCategoryMapper.deleteById(id) != 1) {
            throw new BusinessException(500, "删除图库分类失败");
        }
        log.info("删除图库分类成功，分类编号: {}, 编码: {}", id, categoryDO.getCategoryCode());
    }

    @Override
    public GalleryCategoryDO getRequiredEnabledCategory(String code) {
        String normalized = normalizeCode(code);
        GalleryCategoryDO categoryDO = galleryCategoryMapper.selectOne(
                Wrappers.<GalleryCategoryDO>lambdaQuery()
                        .eq(GalleryCategoryDO::getCategoryCode, normalized)
                        .eq(GalleryCategoryDO::getStatus, CommonStatus.ENABLED.getCode()));
        if (categoryDO == null) {
            throw new BusinessException(400, "图库分类不存在或已禁用");
        }
        return categoryDO;
    }

    @Override
    public Map<String, String> getEnabledCategoryNames() {
        List<GalleryCategoryDO> categories = galleryCategoryMapper.selectList(
                Wrappers.<GalleryCategoryDO>lambdaQuery()
                        .eq(GalleryCategoryDO::getStatus, CommonStatus.ENABLED.getCode()));
        Map<String, String> nameMap = new HashMap<>(categories.size());
        for (GalleryCategoryDO categoryDO : categories) {
            nameMap.put(categoryDO.getCategoryCode(), categoryDO.getCategoryName());
        }
        return nameMap;
    }

    @Override
    public GalleryCategoryDO getCategoryByCode(String code) {
        String normalized = normalizeCode(code);
        GalleryCategoryDO categoryDO = galleryCategoryMapper.selectOne(
                Wrappers.<GalleryCategoryDO>lambdaQuery()
                        .eq(GalleryCategoryDO::getCategoryCode, normalized));
        if (categoryDO == null) {
            throw new BusinessException(404, "图库分类不存在");
        }
        return categoryDO;
    }

    @Override
    public Map<String, String> getAllCategoryNames() {
        List<GalleryCategoryDO> categories = galleryCategoryMapper.selectList(null);
        Map<String, String> nameMap = new HashMap<>(categories.size());
        for (GalleryCategoryDO categoryDO : categories) {
            nameMap.put(categoryDO.getCategoryCode(), categoryDO.getCategoryName());
        }
        return nameMap;
    }

    private GalleryCategoryDO getRequiredCategory(Long id) {
        GalleryCategoryDO categoryDO = galleryCategoryMapper.selectById(id);
        if (categoryDO == null) {
            throw new BusinessException(404, "图库分类不存在");
        }
        return categoryDO;
    }

    /**
     * 统计各分组下的图片数量，一次查询完成，避免逐分类查询。
     */
    private Map<String, Long> countImagesByGroup() {
        List<GalleryImageDO> images = galleryImageMapper.selectList(
                Wrappers.<GalleryImageDO>lambdaQuery()
                        .select(GalleryImageDO::getImageGroup));
        Map<String, Long> countMap = new HashMap<>();
        for (GalleryImageDO imageDO : images) {
            String group = imageDO.getImageGroup();
            if (StringUtils.hasText(group)) {
                countMap.merge(group, 1L, Long::sum);
            }
        }
        return countMap;
    }

    private String normalizeCode(String code) {
        return code.trim().toUpperCase(Locale.ROOT);
    }

    private String trimToNull(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }

    private void validateStatus(Integer status) {
        if (status == null) {
            return;
        }
        try {
            CommonStatus.fromCode(status);
        } catch (IllegalArgumentException exception) {
            throw new BusinessException(400, "分类状态只能是 0 或 1");
        }
    }
}
