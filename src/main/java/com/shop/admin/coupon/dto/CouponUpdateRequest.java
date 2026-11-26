package com.shop.admin.coupon.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 修改优惠券请求。
 *
 * @author shop-admin
 */
@Getter
@Setter
@Schema(description = "修改优惠券请求")
public class CouponUpdateRequest {

    @NotBlank(message = "优惠券名称不能为空")
    @Size(max = 100, message = "优惠券名称长度不能超过 100 位")
    @Schema(description = "优惠券名称", example = "新人满减券")
    private String name;

    @NotBlank(message = "优惠券类型不能为空")
    @Pattern(regexp = "^(AMOUNT|DISCOUNT)$", message = "优惠券类型只能是 AMOUNT 或 DISCOUNT")
    @Schema(description = "类型，AMOUNT 满减券，DISCOUNT 折扣券", example = "AMOUNT")
    private String couponType;

    @NotNull(message = "优惠值不能为空")
    @DecimalMin(value = "0.01", message = "优惠值不能小于 0.01")
    @Digits(integer = 8, fraction = 2, message = "优惠值格式不正确")
    @Schema(description = "满减金额或折扣值，折扣券 8.5 表示 8.5 折", example = "20.00")
    private BigDecimal discountValue;

    @DecimalMin(value = "0.00", message = "使用门槛不能小于 0")
    @Digits(integer = 10, fraction = 2, message = "使用门槛格式不正确")
    @Schema(description = "使用门槛，0 表示无门槛", example = "100.00")
    private BigDecimal thresholdAmount;

    @NotNull(message = "发行总量不能为空")
    @Min(value = 0, message = "发行总量不能小于 0")
    @Max(value = 999999, message = "发行总量不能超过 999999")
    @Schema(description = "发行总量", example = "1000")
    private Integer totalCount;

    @NotNull(message = "有效期开始时间不能为空")
    @Schema(description = "有效期开始时间，格式 yyyy-MM-dd HH:mm:ss", example = "2026-11-01 00:00:00")
    private LocalDateTime validStartTime;

    @NotNull(message = "有效期结束时间不能为空")
    @Schema(description = "有效期结束时间，格式 yyyy-MM-dd HH:mm:ss", example = "2026-12-31 23:59:59")
    private LocalDateTime validEndTime;

    @Min(value = 0, message = "状态只能是 0 或 1")
    @Max(value = 1, message = "状态只能是 0 或 1")
    @Schema(description = "状态，0 禁用，1 启用", example = "1")
    private Integer status;

    @Size(max = 500, message = "使用说明长度不能超过 500 位")
    @Schema(description = "使用说明")
    private String description;
}
