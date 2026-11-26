package com.shop.admin.gallery.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shop.admin.gallery.entity.GalleryImageDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 图库图片数据访问接口。
 *
 * @author shop-admin
 */
@Mapper
public interface GalleryImageMapper extends BaseMapper<GalleryImageDO> {
}
