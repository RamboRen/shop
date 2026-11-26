package com.shop.admin.product.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.shop.admin.common.constant.CommonConstants;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 商品数据对象。
 *
 * @author shop-admin
 */
@Getter
@Setter
@TableName(value = "shop_product", autoResultMap = true)
public class ProductDO {

    /**
     * 主键。
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 商品编码。
     */
    private String productCode;

    /**
     * 分类编号。
     */
    private Long categoryId;

    /**
     * 商品名称。
     */
    private String name;

    /**
     * 商品副标题。
     */
    private String subtitle;

    /**
     * 主图地址。
     */
    private String mainImage;

    /**
     * 附图地址列表，数据库中以 JSON 文本存储。
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> imageUrls;

    /**
     * 销售价。
     */
    private BigDecimal price;

    /**
     * 划线原价。
     */
    private BigDecimal originalPrice;

    /**
     * 库存数量。
     */
    private Integer stock;

    /**
     * 累计销量。
     */
    private Long salesVolume;

    /**
     * 状态，0 下架，1 上架。
     */
    private Integer status;

    /**
     * 排序值，数值越小越靠前。
     */
    private Integer sort;

    /**
     * 商品详情。
     */
    private String detail;

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
