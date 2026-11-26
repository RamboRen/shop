package com.shop.admin.stats.vo;

import com.shop.admin.common.enums.OrderPayType;
import com.shop.admin.stats.model.PayTypeStatDO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 支付方式分布视图对象。
 *
 * @author shop-admin
 */
@Getter
@Setter
@Schema(description = "支付方式分布")
public class PayTypeStatVO {

    @Schema(description = "支付方式编码，未付款订单为 UNKNOWN", example = "WECHAT")
    private String payType;

    @Schema(description = "支付方式名称", example = "微信支付")
    private String payTypeName;

    @Schema(description = "该支付方式订单数", example = "12")
    private Long orderCount;

    @Schema(description = "该支付方式应付金额", example = "12800.00")
    private BigDecimal payAmount;

    /**
     * 将数据对象转换为视图对象。未付款订单的支付方式编码展示为 UNKNOWN。
     *
     * @param statDO 支付方式统计数据对象
     * @return 支付方式分布视图对象
     */
    public static PayTypeStatVO from(PayTypeStatDO statDO) {
        String code = statDO.getPayType();
        boolean unknown = code == null || code.isBlank();
        PayTypeStatVO vo = new PayTypeStatVO();
        vo.setPayType(unknown ? "UNKNOWN" : code);
        vo.setPayTypeName(unknown ? "未付款" : OrderPayType.describe(code));
        vo.setOrderCount(statDO.getOrderCount() == null ? 0L : statDO.getOrderCount());
        vo.setPayAmount(statDO.getPayAmount() == null ? BigDecimal.ZERO : statDO.getPayAmount());
        return vo;
    }
}
