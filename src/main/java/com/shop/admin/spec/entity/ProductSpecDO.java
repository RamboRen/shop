package com.shop.admin.spec.entity;

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

import java.time.LocalDateTime;
import java.util.List;

/**
 * 商品规格数据对象。
 *
 * @author shop-admin
 */
@Getter
@Setter
@TableName(value = "shop_product_spec", autoResultMap = true)
public class ProductSpecDO {

    /**
     * 主键。
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 规格名称，例如颜色、尺寸。
     */
    private String name;

    /**
     * 规格值列表，数据库中以 JSON 文本存储。
     */
    @TableField(value = "spec_values", typeHandler = JacksonTypeHandler.class)
    private List<String> values;

    /**
     * 状态，0 禁用，1 启用。
     */
    private Integer status;

    /**
     * 排序值，数值越小越靠前。
     */
    private Integer sort;

    /**
     * 备注。
     */
    private String remark;

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
