package com.shop.admin.stats.vo;

import com.shop.admin.stats.model.StatsOverviewDO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 后台概览统计视图对象。
 *
 * @author shop-admin
 */
@Getter
@Setter
@Schema(description = "后台概览统计")
public class StatsOverviewVO {

    @Schema(description = "累计销售额", example = "128900.00")
    private BigDecimal totalSalesAmount;

    @Schema(description = "今日销售额", example = "3200.00")
    private BigDecimal todaySalesAmount;

    @Schema(description = "累计有效订单数", example = "320")
    private Long totalOrderCount;

    @Schema(description = "今日有效订单数", example = "12")
    private Long todayOrderCount;

    @Schema(description = "商品总数", example = "36")
    private Long productCount;

    @Schema(description = "上架商品数", example = "28")
    private Long onSaleProductCount;

    @Schema(description = "分类总数", example = "5")
    private Long categoryCount;

    @Schema(description = "图库图片总数", example = "56")
    private Long galleryCount;

    @Schema(description = "管理员总数", example = "3")
    private Long adminCount;

    @Schema(description = "低库存商品数", example = "2")
    private Long lowStockProductCount;

    @Schema(description = "今日新增商品数", example = "1")
    private Long todayNewProductCount;

    @Schema(description = "累计销量", example = "1024")
    private Long totalSalesVolume;

    /**
     * 将数据对象转换为视图对象。
     *
     * @param overviewDO 概览数据对象
     * @return 概览视图对象
     */
    public static StatsOverviewVO from(StatsOverviewDO overviewDO) {
        StatsOverviewVO overviewVO = new StatsOverviewVO();
        overviewVO.setTotalSalesAmount(overviewDO.getTotalSalesAmount());
        overviewVO.setTodaySalesAmount(overviewDO.getTodaySalesAmount());
        overviewVO.setTotalOrderCount(overviewDO.getTotalOrderCount());
        overviewVO.setTodayOrderCount(overviewDO.getTodayOrderCount());
        overviewVO.setProductCount(overviewDO.getProductCount());
        overviewVO.setOnSaleProductCount(overviewDO.getOnSaleProductCount());
        overviewVO.setCategoryCount(overviewDO.getCategoryCount());
        overviewVO.setGalleryCount(overviewDO.getGalleryCount());
        overviewVO.setAdminCount(overviewDO.getAdminCount());
        overviewVO.setLowStockProductCount(overviewDO.getLowStockProductCount());
        overviewVO.setTodayNewProductCount(overviewDO.getTodayNewProductCount());
        overviewVO.setTotalSalesVolume(overviewDO.getTotalSalesVolume());
        return overviewVO;
    }
}
