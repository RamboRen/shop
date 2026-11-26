package com.shop.admin.coupon.service;

import com.shop.admin.common.api.PageResult;
import com.shop.admin.coupon.dto.CouponCreateRequest;
import com.shop.admin.coupon.dto.CouponQueryRequest;
import com.shop.admin.coupon.dto.CouponStatusRequest;
import com.shop.admin.coupon.dto.CouponUpdateRequest;
import com.shop.admin.coupon.vo.CouponVO;

/**
 * 优惠券服务接口。
 *
 * @author shop-admin
 */
public interface CouponService {

    /**
     * 分页查询优惠券。
     *
     * @param request 查询条件
     * @return 优惠券分页结果
     */
    PageResult<CouponVO> pageCoupons(CouponQueryRequest request);

    /**
     * 查询优惠券详情。
     *
     * @param id 优惠券编号
     * @return 优惠券信息
     */
    CouponVO getCoupon(Long id);

    /**
     * 新增优惠券。
     *
     * @param request 新增请求
     * @return 新优惠券编号
     */
    Long createCoupon(CouponCreateRequest request);

    /**
     * 修改优惠券。
     *
     * @param id      优惠券编号
     * @param request 修改请求
     */
    void updateCoupon(Long id, CouponUpdateRequest request);

    /**
     * 删除优惠券。
     *
     * @param id 优惠券编号
     */
    void deleteCoupon(Long id);

    /**
     * 修改优惠券状态。
     *
     * @param id      优惠券编号
     * @param request 状态请求
     */
    void updateStatus(Long id, CouponStatusRequest request);
}
