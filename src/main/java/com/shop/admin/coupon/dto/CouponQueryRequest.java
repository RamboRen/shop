package com.shop.admin.coupon.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * 优惠券分页查询请求。
 *
 * @author shop-admin
 */
@Getter
@Setter
@Schema(description = "优惠券分页查询请求")
public class CouponQueryRequest {

    @Min(value = 1, message = "页码不能小于 1")
    @Schema(description = "页码", example = "1")
    private Integer pageNum = 1;

    @Min(value = 1, message = "每页条数不能小于 1")
    @Max(value = 100, message = "每页条数不能超过 100")
    @Schema(description = "每页条数", example = "10")
    private Integer pageSize = 10;

    @Size(max = 100, message = "关键字长度不能超过 100 位")
    @Schema(description = "优惠券名称或编码关键字")
    private String keyword;

    @Pattern(regexp = "^(AMOUNT|DISCOUNT)$", message = "优惠券类型只能是 AMOUNT 或 DISCOUNT")
    @Schema(description = "类型，AMOUNT 满减券，DISCOUNT 折扣券")
    private String couponType;

    @Min(value = 0, message = "状态只能是 0 或 1")
    @Max(value = 1, message = "状态只能是 0 或 1")
    @Schema(description = "状态，0 禁用，1 启用")
    private Integer status;
}
