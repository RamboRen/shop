package com.shop.admin.gallery.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.shop.admin.common.constant.CommonConstants;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 图库图片数据对象。
 *
 * @author shop-admin
 */
@Getter
@Setter
@TableName("shop_gallery_image")
public class GalleryImageDO {

    /**
     * 主键。
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 服务器存储文件名。
     */
    private String fileName;

    /**
     * 上传时的原始文件名。
     */
    private String originalName;

    /**
     * 对外访问地址。
     */
    private String url;

    /**
     * 相对上传根目录的存储路径。
     */
    private String relativePath;

    /**
     * 图片媒体类型。
     */
    private String contentType;

    /**
     * 文件大小，单位字节。
     */
    private Long fileSize;

    /**
     * 图片宽度，单位像素。
     */
    private Integer width;

    /**
     * 图片高度，单位像素。
     */
    private Integer height;

    /**
     * 图库分组编码。
     */
    private String imageGroup;

    /**
     * 创建时间。
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间。
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 逻辑删除标识，0 未删除，1 已删除。
     */
    @TableLogic
    private Integer deleted = CommonConstants.NOT_DELETED;
}
