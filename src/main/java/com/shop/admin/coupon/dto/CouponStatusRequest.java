package com.shop.admin.coupon.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * 优惠券状态修改请求。
 *
 * @author shop-admin
 */
@Getter
@Setter
@Schema(description = "优惠券状态修改请求")
public class CouponStatusRequest {

    @NotNull(message = "状态不能为空")
    @Min(value = 0, message = "状态只能是 0 或 1")
    @Max(value = 1, message = "状态只能是 0 或 1")
    @Schema(description = "状态，0 禁用，1 启用", example = "1")
    private Integer status;
}
