package com.shop.admin.order.controller;

import com.shop.admin.common.api.ApiResult;
import com.shop.admin.common.api.PageResult;
import com.shop.admin.order.dto.OrderQueryRequest;
import com.shop.admin.order.dto.OrderStatusRequest;
import com.shop.admin.order.service.OrderService;
import com.shop.admin.order.vo.OrderDetailVO;
import com.shop.admin.order.vo.OrderVO;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 订单管理接口。
 *
 * @author shop-admin
 */
@RestController
@RequestMapping("/api/admin/orders")
@RequiredArgsConstructor
@PreAuthorize("hasRole('SUPER_ADMIN') or hasAuthority('order:view')")
@Tag(name = "订单管理", description = "订单分页查询、详情、状态流转与删除")
@SecurityRequirement(name = "bearerAuth")
public class OrderController {

    private final OrderService orderService;

    /**
     * 分页查询订单。
     *
     * @param request 查询条件
     * @return 订单分页结果
     */
    @GetMapping
    @Operation(summary = "分页查询订单")
    public ApiResult<PageResult<OrderVO>> pageOrders(@Valid @ParameterObject OrderQueryRequest request) {
        return ApiResult.success(orderService.pageOrders(request));
    }

    /**
     * 查询订单详情。
     *
     * @param id 订单编号
     * @return 订单详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "查询订单详情", description = "返回订单基础信息和商品明细")
    public ApiResult<OrderDetailVO> getOrder(@PathVariable Long id) {
        return ApiResult.success(orderService.getOrder(id));
    }

    /**
     * 修改订单状态。
     *
     * @param id      订单编号
     * @param request 状态修改请求
     * @return 操作结果
     */
    @PutMapping("/{id}/status")
    @Operation(summary = "修改订单状态",
            description = "支持待付款->待发货/已取消、待发货->待收货/已取消、待收货->已完成")
    public ApiResult<Void> updateStatus(@PathVariable Long id,
                                        @Valid @RequestBody OrderStatusRequest request) {
        orderService.updateStatus(id, request);
        return ApiResult.success();
    }

    /**
     * 删除订单。
     *
     * @param id 订单编号
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除订单", description = "仅已完成或已取消的订单可以删除")
    public ApiResult<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ApiResult.success();
    }
}
