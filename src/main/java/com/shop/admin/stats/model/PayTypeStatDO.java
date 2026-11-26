package com.shop.admin.stats.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 按支付方式聚合的统计数据对象。
 *
 * @author shop-admin
 */
@Getter
@Setter
public class PayTypeStatDO {

    /**
     * 支付方式编码，未付款订单为空。
     */
    private String payType;

    /**
     * 订单数量。
     */
    private Long orderCount;

    /**
     * 应付金额合计。
     */
    private BigDecimal payAmount;
}
