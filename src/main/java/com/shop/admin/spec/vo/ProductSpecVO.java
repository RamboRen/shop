package com.shop.admin.spec.vo;

import com.shop.admin.common.enums.CommonStatus;
import com.shop.admin.spec.entity.ProductSpecDO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 商品规格视图对象。
 *
 * @author shop-admin
 */
@Getter
@Setter
@Schema(description = "商品规格信息")
public class ProductSpecVO {

    @Schema(description = "规格编号", example = "1")
    private Long id;

    @Schema(description = "规格名称", example = "颜色")
    private String name;

    @Schema(description = "规格值列表", example = "[\"黑色\",\"白色\"]")
    private List<String> values;

    @Schema(description = "状态，0 禁用，1 启用", example = "1")
    private Integer status;

    @Schema(description = "状态名称", example = "启用")
    private String statusName;

    @Schema(description = "排序值", example = "1")
    private Integer sort;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    /**
     * 将数据对象转换为视图对象。
     *
     * @param specDO 商品规格数据对象
     * @return 商品规格视图对象
     */
    public static ProductSpecVO from(ProductSpecDO specDO) {
        ProductSpecVO specVO = new ProductSpecVO();
        specVO.setId(specDO.getId());
        specVO.setName(specDO.getName());
        specVO.setValues(specDO.getValues());
        specVO.setStatus(specDO.getStatus());
        specVO.setStatusName(CommonStatus.fromCode(specDO.getStatus()).getDescription());
        specVO.setSort(specDO.getSort());
        specVO.setRemark(specDO.getRemark());
        specVO.setCreateTime(specDO.getCreateTime());
        specVO.setUpdateTime(specDO.getUpdateTime());
        return specVO;
    }
}
