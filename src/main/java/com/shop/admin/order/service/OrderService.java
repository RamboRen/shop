package com.shop.admin.order.service;

import com.shop.admin.common.api.PageResult;
import com.shop.admin.order.dto.OrderQueryRequest;
import com.shop.admin.order.dto.OrderStatusRequest;
import com.shop.admin.order.vo.OrderDetailVO;
import com.shop.admin.order.vo.OrderVO;

/**
 * 订单管理服务。
 *
 * @author shop-admin
 */
public interface OrderService {

    /**
     * 分页查询订单。
     *
     * @param request 查询条件
     * @return 订单分页结果
     */
    PageResult<OrderVO> pageOrders(OrderQueryRequest request);

    /**
     * 查询订单详情。
     *
     * @param id 订单编号
     * @return 订单详情
     */
    OrderDetailVO getOrder(Long id);

    /**
     * 修改订单状态。
     *
     * @param id      订单编号
     * @param request 状态修改请求
     */
    void updateStatus(Long id, OrderStatusRequest request);

    /**
     * 删除已完成或已取消的订单。
     *
     * @param id 订单编号
     */
    void deleteOrder(Long id);
}
