package com.shop.admin.gallery.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shop.admin.common.api.PageResult;
import com.shop.admin.common.constant.CommonConstants;
import com.shop.admin.common.exception.BusinessException;
import com.shop.admin.config.UploadProperties;
import com.shop.admin.gallery.category.entity.GalleryCategoryDO;
import com.shop.admin.gallery.category.service.GalleryCategoryService;
import com.shop.admin.gallery.dto.GalleryImageRenameRequest;
import com.shop.admin.gallery.dto.GalleryQueryRequest;
import com.shop.admin.gallery.entity.GalleryImageDO;
import com.shop.admin.gallery.mapper.GalleryImageMapper;
import com.shop.admin.gallery.service.GalleryService;
import com.shop.admin.gallery.vo.GalleryImageVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Locale;
import java.util.UUID;

/**
 * 图库服务实现。
 *
 * @author shop-admin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GalleryServiceImpl implements GalleryService {

    private static final String UPLOAD_URL_PREFIX = "/uploads/";

    private static final DateTimeFormatter DIRECTORY_DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy/MM/dd");

    private static final int MAX_IMAGE_WIDTH = 10000;

    private static final int MAX_IMAGE_HEIGHT = 10000;

    private static final long MAX_IMAGE_PIXELS = 40000000L;

    private static final int MAX_ORIGINAL_NAME_LENGTH = 255;

    private final GalleryImageMapper galleryImageMapper;

    private final GalleryCategoryService galleryCategoryService;

    private final UploadProperties uploadProperties;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public GalleryImageVO uploadImage(MultipartFile file, String categoryCode) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(400, "请选择要上传的图片");
        }
        if (file.getSize() > uploadProperties.getMaxFileSize().toBytes()) {
            throw new BusinessException(400, "图片大小超过限制");
        }
        GalleryCategoryDO categoryDO = parseCategoryCode(categoryCode);
        ImageMetadata imageMetadata = readImageMetadata(file);

        String dateDirectory = LocalDate.now().format(DIRECTORY_DATE_FORMATTER);
        String storedFileName = UUID.randomUUID().toString().replace("-", "")
                + "." + imageMetadata.imageType().extension();
        String relativePath = dateDirectory + "/" + storedFileName;
        Path targetPath = resolveSafePath(relativePath);

        try {
            Files.createDirectories(targetPath.getParent());
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException exception) {
            log.error("保存上传图片失败，路径: {}", targetPath, exception);
            deleteFileQuietly(targetPath);
            throw new BusinessException(500, "保存图片失败");
        }

        GalleryImageDO imageDO = new GalleryImageDO();
        imageDO.setFileName(storedFileName);
        imageDO.setOriginalName(sanitizeOriginalName(file.getOriginalFilename(),
                imageMetadata.imageType().extension()));
        imageDO.setUrl(UPLOAD_URL_PREFIX + relativePath);
        imageDO.setRelativePath(relativePath);
        imageDO.setContentType(imageMetadata.imageType().contentType());
        imageDO.setFileSize(file.getSize());
        imageDO.setWidth(imageMetadata.width());
        imageDO.setHeight(imageMetadata.height());
        imageDO.setImageGroup(categoryDO.getCategoryCode());
        try {
            if (galleryImageMapper.insert(imageDO) != 1) {
                throw new BusinessException(500, "保存图片记录失败");
            }
        } catch (RuntimeException exception) {
            deleteFileQuietly(targetPath);
            throw exception;
        }
        log.info("上传图片成功，图片编号: {}, 原始文件名: {}", imageDO.getId(), imageDO.getOriginalName());
        return GalleryImageVO.from(imageDO, categoryDO.getCategoryName());
    }

    @Override
    public PageResult<GalleryImageVO> pageImages(GalleryQueryRequest request) {
        int pageNum = request.getPageNum() == null ? CommonConstants.DEFAULT_PAGE_NUM : request.getPageNum();
        int pageSize = request.getPageSize() == null ? CommonConstants.DEFAULT_PAGE_SIZE : request.getPageSize();
        LambdaQueryWrapper<GalleryImageDO> wrapper = Wrappers.<GalleryImageDO>lambdaQuery();
        String keyword = trimToNull(request.getKeyword());
        if (keyword != null) {
            wrapper.like(GalleryImageDO::getOriginalName, keyword);
        }
        if (StringUtils.hasText(request.getImageGroup())) {
            GalleryCategoryDO categoryDO = parseCategoryCode(request.getImageGroup());
            wrapper.eq(GalleryImageDO::getImageGroup, categoryDO.getCategoryCode());
        }
        wrapper.orderByDesc(GalleryImageDO::getCreateTime);

        Page<GalleryImageDO> imagePage = galleryImageMapper.selectPage(
                new Page<>(pageNum, pageSize), wrapper);
        Map<String, String> categoryNames = galleryCategoryService.getEnabledCategoryNames();
        List<GalleryImageVO> records = imagePage.getRecords().stream()
                .map(imageDO -> GalleryImageVO.from(
                        imageDO, categoryNames.get(imageDO.getImageGroup())))
                .toList();
        return PageResult.of(imagePage.getTotal(), imagePage.getCurrent(), imagePage.getSize(), records);
    }

    @Override
    public PageResult<GalleryImageVO> pageImagesByCategory(String categoryCode, GalleryQueryRequest request) {
        GalleryCategoryDO categoryDO = galleryCategoryService.getCategoryByCode(categoryCode);
        int pageNum = request.getPageNum() == null ? CommonConstants.DEFAULT_PAGE_NUM : request.getPageNum();
        int pageSize = request.getPageSize() == null ? CommonConstants.DEFAULT_PAGE_SIZE : request.getPageSize();
        LambdaQueryWrapper<GalleryImageDO> wrapper = Wrappers.<GalleryImageDO>lambdaQuery()
                .eq(GalleryImageDO::getImageGroup, categoryDO.getCategoryCode());
        String keyword = trimToNull(request.getKeyword());
        if (keyword != null) {
            wrapper.like(GalleryImageDO::getOriginalName, keyword);
        }
        wrapper.orderByDesc(GalleryImageDO::getCreateTime);

        Page<GalleryImageDO> imagePage = galleryImageMapper.selectPage(
                new Page<>(pageNum, pageSize), wrapper);
        Map<String, String> categoryNames = galleryCategoryService.getAllCategoryNames();
        List<GalleryImageVO> records = imagePage.getRecords().stream()
                .map(imageDO -> GalleryImageVO.from(
                        imageDO, categoryNames.get(imageDO.getImageGroup())))
                .toList();
        return PageResult.of(imagePage.getTotal(), imagePage.getCurrent(), imagePage.getSize(), records);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public GalleryImageVO renameImage(Long id, GalleryImageRenameRequest request) {
        GalleryImageDO imageDO = getRequiredImage(id);
        String newName = sanitizeRenamedName(request.getOriginalName());
        GalleryImageDO updateDO = new GalleryImageDO();
        updateDO.setId(id);
        updateDO.setOriginalName(newName);
        if (galleryImageMapper.updateById(updateDO) != 1) {
            throw new BusinessException(500, "修改图片名称失败");
        }
        imageDO.setOriginalName(newName);
        log.info("修改图片名称成功，图片编号: {}, 新名称: {}", id, newName);
        return GalleryImageVO.from(imageDO,
                galleryCategoryService.getAllCategoryNames().get(imageDO.getImageGroup()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteImage(Long id) {
        GalleryImageDO imageDO = getRequiredImage(id);
        if (galleryImageMapper.deleteById(id) != 1) {
            throw new BusinessException(500, "删除图片失败");
        }
        deleteStoredFile(imageDO.getRelativePath());
        log.info("删除图片成功，图片编号: {}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDeleteImages(List<Long> ids) {
        List<Long> distinctIds = ids.stream().distinct().toList();
        if (distinctIds.size() != ids.size()) {
            throw new BusinessException(400, "图片编号不能重复");
        }
        List<GalleryImageDO> imageDOS = galleryImageMapper.selectBatchIds(distinctIds);
        if (imageDOS.size() != distinctIds.size()) {
            throw new BusinessException(404, "部分图片不存在或已被删除");
        }
        if (galleryImageMapper.deleteBatchIds(distinctIds) != distinctIds.size()) {
            throw new BusinessException(500, "批量删除图片失败");
        }
        for (GalleryImageDO imageDO : imageDOS) {
            deleteStoredFile(imageDO.getRelativePath());
        }
        log.info("批量删除图片成功，图片数量: {}", distinctIds.size());
    }

    private GalleryImageDO getRequiredImage(Long id) {
        GalleryImageDO imageDO = galleryImageMapper.selectById(id);
        if (imageDO == null) {
            throw new BusinessException(404, "图片不存在");
        }
        return imageDO;
    }

    private ImageMetadata readImageMetadata(MultipartFile file) {
        ImageReader imageReader = null;
        try (ImageInputStream imageInputStream = ImageIO.createImageInputStream(file.getInputStream())) {
            if (imageInputStream == null) {
                throw new BusinessException(400, "无法读取图片内容");
            }
            Iterator<ImageReader> readers = ImageIO.getImageReaders(imageInputStream);
            if (!readers.hasNext()) {
                throw new BusinessException(400, "文件不是有效的图片");
            }
            imageReader = readers.next();
            imageReader.setInput(imageInputStream, true, true);
            String formatName = imageReader.getFormatName();
            SupportedImageType imageType = SupportedImageType.fromFormatName(formatName);
            if (imageType == null
                    || !uploadProperties.getAllowedContentTypes().contains(imageType.contentType())) {
                throw new BusinessException(400, "仅支持 JPG、PNG、GIF 格式图片");
            }
            int width = imageReader.getWidth(0);
            int height = imageReader.getHeight(0);
            if (width <= 0 || height <= 0
                    || width > MAX_IMAGE_WIDTH || height > MAX_IMAGE_HEIGHT
                    || (long) width * height > MAX_IMAGE_PIXELS) {
                throw new BusinessException(400, "图片尺寸超出允许范围");
            }
            return new ImageMetadata(imageType, width, height);
        } catch (BusinessException exception) {
            throw exception;
        } catch (IOException exception) {
            log.warn("读取上传图片失败", exception);
            throw new BusinessException(400, "读取图片失败");
        } finally {
            if (imageReader != null) {
                imageReader.dispose();
            }
        }
    }

    private GalleryCategoryDO parseCategoryCode(String categoryCode) {
        String code = StringUtils.hasText(categoryCode) ? categoryCode.trim() : "OTHER";
        return galleryCategoryService.getRequiredEnabledCategory(code);
    }

    private String sanitizeOriginalName(String originalFilename, String extension) {
        if (!StringUtils.hasText(originalFilename)) {
            return "image." + extension;
        }
        String normalized = originalFilename.replace('\\', '/');
        int lastSlashIndex = normalized.lastIndexOf('/');
        if (lastSlashIndex >= 0) {
            normalized = normalized.substring(lastSlashIndex + 1);
        }
        String sanitized = normalized.replaceAll("\\p{Cntrl}", "").trim();
        if (!StringUtils.hasText(sanitized)) {
            return "image." + extension;
        }
        if (sanitized.length() > MAX_ORIGINAL_NAME_LENGTH) {
            sanitized = sanitized.substring(0, MAX_ORIGINAL_NAME_LENGTH);
        }
        return sanitized;
    }

    /**
     * 清洗修改后的名称：去掉路径前缀与控制字符，限制长度。
     * DTO 层已校验非空，这里仍做兜底防止服务层被直接调用时存入空名称。
     */
    private String sanitizeRenamedName(String name) {
        if (!StringUtils.hasText(name)) {
            throw new BusinessException(400, "图片名称不能为空");
        }
        String normalized = name.replace('\\', '/');
        int lastSlashIndex = normalized.lastIndexOf('/');
        if (lastSlashIndex >= 0) {
            normalized = normalized.substring(lastSlashIndex + 1);
        }
        String sanitized = normalized.replaceAll("\\p{Cntrl}", "").trim();
        if (!StringUtils.hasText(sanitized)) {
            throw new BusinessException(400, "图片名称不能为空");
        }
        return sanitized.length() > MAX_ORIGINAL_NAME_LENGTH
                ? sanitized.substring(0, MAX_ORIGINAL_NAME_LENGTH)
                : sanitized;
    }

    private Path resolveSafePath(String relativePath) {
        Path uploadRoot = Paths.get(uploadProperties.getPath()).toAbsolutePath().normalize();
        Path targetPath = uploadRoot.resolve(relativePath).normalize();
        if (!targetPath.startsWith(uploadRoot)) {
            throw new BusinessException(400, "非法的文件路径");
        }
        return targetPath;
    }

    private void deleteStoredFile(String relativePath) {
        if (!StringUtils.hasText(relativePath)) {
            return;
        }
        deleteFileQuietly(resolveSafePath(relativePath));
    }

    private void deleteFileQuietly(Path targetPath) {
        try {
            Files.deleteIfExists(targetPath);
        } catch (IOException exception) {
            log.warn("删除本地图片文件失败，路径: {}", targetPath, exception);
        }
    }

    private String trimToNull(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }

    /**
     * 图片元信息。
     *
     * @param imageType 图片类型
     * @param width     宽度
     * @param height    高度
     */
    private record ImageMetadata(SupportedImageType imageType, int width, int height) {
    }

    /**
     * 支持的图片类型。
     */
    private enum SupportedImageType {

        /**
         * JPEG 图片。
         */
        JPEG("jpg", "image/jpeg"),

        /**
         * PNG 图片。
         */
        PNG("png", "image/png"),

        /**
         * GIF 图片。
         */
        GIF("gif", "image/gif");

        private final String extension;

        private final String contentType;

        SupportedImageType(String extension, String contentType) {
            this.extension = extension;
            this.contentType = contentType;
        }

        public String extension() {
            return extension;
        }

        public String contentType() {
            return contentType;
        }

        /**
         * 根据 ImageIO 格式名称匹配图片类型。
         *
         * @param formatName 格式名称
         * @return 图片类型，不支持时返回 null
         */
        public static SupportedImageType fromFormatName(String formatName) {
            if (!StringUtils.hasText(formatName)) {
                return null;
            }
            String normalized = formatName.toLowerCase(Locale.ROOT);
            return switch (normalized) {
                case "jpeg", "jpg" -> JPEG;
                case "png" -> PNG;
                case "gif" -> GIF;
                default -> null;
            };
        }
    }
}
