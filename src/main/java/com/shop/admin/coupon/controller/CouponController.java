package com.shop.admin.coupon.controller;

import com.shop.admin.common.api.ApiResult;
import com.shop.admin.common.api.PageResult;
import com.shop.admin.coupon.dto.CouponCreateRequest;
import com.shop.admin.coupon.dto.CouponQueryRequest;
import com.shop.admin.coupon.dto.CouponStatusRequest;
import com.shop.admin.coupon.dto.CouponUpdateRequest;
import com.shop.admin.coupon.service.CouponService;
import com.shop.admin.coupon.vo.CouponVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 优惠券管理接口。
 *
 * @author shop-admin
 */
@RestController
@RequestMapping("/api/admin/coupons")
@RequiredArgsConstructor
@PreAuthorize("hasRole('SUPER_ADMIN') or hasAuthority('product:coupon')")
@Tag(name = "优惠券管理", description = "优惠券的查询、新增、修改、删除与启停")
@SecurityRequirement(name = "bearerAuth")
public class CouponController {

    private final CouponService couponService;

    /**
     * 分页查询优惠券。
     *
     * @param request 查询条件
     * @return 优惠券分页结果
     */
    @GetMapping
    @Operation(summary = "分页查询优惠券")
    public ApiResult<PageResult<CouponVO>> pageCoupons(@Valid @ParameterObject CouponQueryRequest request) {
        return ApiResult.success(couponService.pageCoupons(request));
    }

    /**
     * 查询优惠券详情。
     *
     * @param id 优惠券编号
     * @return 优惠券信息
     */
    @GetMapping("/{id}")
    @Operation(summary = "查询优惠券详情")
    public ApiResult<CouponVO> getCoupon(@PathVariable Long id) {
        return ApiResult.success(couponService.getCoupon(id));
    }

    /**
     * 新增优惠券。
     *
     * @param request 新增请求
     * @return 新优惠券编号
     */
    @PostMapping
    @Operation(summary = "新增优惠券")
    public ApiResult<Long> createCoupon(@Valid @RequestBody CouponCreateRequest request) {
        return ApiResult.success(couponService.createCoupon(request));
    }

    /**
     * 修改优惠券。
     *
     * @param id      优惠券编号
     * @param request 修改请求
     * @return 操作结果
     */
    @PutMapping("/{id}")
    @Operation(summary = "修改优惠券")
    public ApiResult<Void> updateCoupon(@PathVariable Long id,
                                        @Valid @RequestBody CouponUpdateRequest request) {
        couponService.updateCoupon(id, request);
        return ApiResult.success();
    }

    /**
     * 删除优惠券。
     *
     * @param id 优惠券编号
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除优惠券")
    public ApiResult<Void> deleteCoupon(@PathVariable Long id) {
        couponService.deleteCoupon(id);
        return ApiResult.success();
    }

    /**
     * 修改优惠券状态。
     *
     * @param id      优惠券编号
     * @param request 状态请求
     * @return 操作结果
     */
    @PutMapping("/{id}/status")
    @Operation(summary = "修改优惠券状态")
    public ApiResult<Void> updateStatus(@PathVariable Long id,
                                        @Valid @RequestBody CouponStatusRequest request) {
        couponService.updateStatus(id, request);
        return ApiResult.success();
    }
}
