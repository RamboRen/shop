package com.shop.admin.user.vo;

import com.shop.admin.user.entity.UserDO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 用户详情视图对象，包含订单统计。
 *
 * @author shop-admin
 */
@Getter
@Setter
@Schema(description = "用户详情")
public class UserDetailVO extends UserVO {

    @Schema(description = "订单数量", example = "3")
    private Long orderCount;

    @Schema(description = "累计消费金额", example = "1200.00")
    private BigDecimal totalSpent;

    /**
     * 将用户及订单统计转换为详情视图对象。
     *
     * @param userDO     用户数据对象
     * @param orderCount 订单数量
     * @param totalSpent 累计消费金额
     * @return 用户详情视图对象
     */
    public static UserDetailVO from(UserDO userDO, Long orderCount, BigDecimal totalSpent) {
        UserDetailVO detailVO = new UserDetailVO();
        detailVO.copyFrom(userDO);
        detailVO.setOrderCount(orderCount);
        detailVO.setTotalSpent(totalSpent);
        return detailVO;
    }
}
