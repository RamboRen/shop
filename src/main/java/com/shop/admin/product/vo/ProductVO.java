package com.shop.admin.product.vo;

import com.shop.admin.common.enums.ProductStatus;
import com.shop.admin.product.entity.ProductDO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 商品视图对象。
 *
 * @author shop-admin
 */
@Getter
@Setter
@Schema(description = "商品信息")
public class ProductVO {

    @Schema(description = "商品编号", example = "1")
    private Long id;

    @Schema(description = "商品编码", example = "SP20260001")
    private String productCode;

    @Schema(description = "分类编号", example = "1")
    private Long categoryId;

    @Schema(description = "分类名称", example = "数码家电")
    private String categoryName;

    @Schema(description = "商品名称", example = "无线蓝牙耳机")
    private String name;

    @Schema(description = "商品副标题")
    private String subtitle;

    @Schema(description = "商品主图地址")
    private String mainImage;

    @Schema(description = "商品附图地址列表")
    private List<String> imageUrls;

    @Schema(description = "销售价", example = "299.00")
    private BigDecimal price;

    @Schema(description = "划线原价", example = "399.00")
    private BigDecimal originalPrice;

    @Schema(description = "库存数量", example = "100")
    private Integer stock;

    @Schema(description = "累计销量", example = "20")
    private Long salesVolume;

    @Schema(description = "状态，0 下架，1 上架", example = "1")
    private Integer status;

    @Schema(description = "状态名称", example = "上架")
    private String statusName;

    @Schema(description = "排序值", example = "1")
    private Integer sort;

    @Schema(description = "商品详情")
    private String detail;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    /**
     * 将数据对象转换为视图对象。
     *
     * @param productDO    商品数据对象
     * @param categoryName 分类名称
     * @return 商品视图对象
     */
    public static ProductVO from(ProductDO productDO, String categoryName) {
        ProductVO productVO = new ProductVO();
        productVO.setId(productDO.getId());
        productVO.setProductCode(productDO.getProductCode());
        productVO.setCategoryId(productDO.getCategoryId());
        productVO.setCategoryName(categoryName);
        productVO.setName(productDO.getName());
        productVO.setSubtitle(productDO.getSubtitle());
        productVO.setMainImage(productDO.getMainImage());
        productVO.setImageUrls(productDO.getImageUrls());
        productVO.setPrice(productDO.getPrice());
        productVO.setOriginalPrice(productDO.getOriginalPrice());
        productVO.setStock(productDO.getStock());
        productVO.setSalesVolume(productDO.getSalesVolume());
        productVO.setStatus(productDO.getStatus());
        productVO.setStatusName(ProductStatus.fromCode(productDO.getStatus()).getDescription());
        productVO.setSort(productDO.getSort());
        productVO.setDetail(productDO.getDetail());
        productVO.setCreateTime(productDO.getCreateTime());
        productVO.setUpdateTime(productDO.getUpdateTime());
        return productVO;
    }
}
