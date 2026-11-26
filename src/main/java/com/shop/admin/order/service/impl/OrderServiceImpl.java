package com.shop.admin.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shop.admin.common.api.PageResult;
import com.shop.admin.common.constant.CommonConstants;
import com.shop.admin.common.enums.OrderStatus;
import com.shop.admin.common.exception.BusinessException;
import com.shop.admin.order.dto.OrderQueryRequest;
import com.shop.admin.order.dto.OrderStatusRequest;
import com.shop.admin.order.entity.OrderDO;
import com.shop.admin.order.entity.OrderItemDO;
import com.shop.admin.order.mapper.OrderItemMapper;
import com.shop.admin.order.mapper.OrderMapper;
import com.shop.admin.order.service.OrderService;
import com.shop.admin.order.vo.OrderDetailVO;
import com.shop.admin.order.vo.OrderVO;
import com.shop.admin.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单管理服务实现。
 *
 * @author shop-admin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;

    private final OrderItemMapper orderItemMapper;

    @Override
    public PageResult<OrderVO> pageOrders(OrderQueryRequest request) {
        validateDateRange(request);
        int pageNum = request.getPageNum() == null ? CommonConstants.DEFAULT_PAGE_NUM : request.getPageNum();
        int pageSize = request.getPageSize() == null ? CommonConstants.DEFAULT_PAGE_SIZE : request.getPageSize();

        LambdaQueryWrapper<OrderDO> wrapper = Wrappers.<OrderDO>lambdaQuery();
        String keyword = request.getKeyword() == null ? null : request.getKeyword().trim();
        if (StringUtils.hasText(keyword)) {
            wrapper.and(query -> query.like(OrderDO::getOrderNo, keyword)
                    .or()
                    .like(OrderDO::getReceiverName, keyword)
                    .or()
                    .like(OrderDO::getReceiverPhone, keyword));
        }
        if (request.getStatus() != null) {
            wrapper.eq(OrderDO::getStatus, request.getStatus());
        }
        if (request.getStartDate() != null) {
            wrapper.ge(OrderDO::getCreateTime, request.getStartDate().atStartOfDay());
        }
        if (request.getEndDate() != null) {
            wrapper.lt(OrderDO::getCreateTime, request.getEndDate().plusDays(1).atStartOfDay());
        }
        wrapper.orderByDesc(OrderDO::getCreateTime).orderByDesc(OrderDO::getId);

        Page<OrderDO> orderPage = orderMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        List<OrderVO> records = orderPage.getRecords().stream()
                .map(OrderVO::from)
                .toList();
        return PageResult.of(orderPage.getTotal(), orderPage.getCurrent(), orderPage.getSize(), records);
    }

    @Override
    public OrderDetailVO getOrder(Long id) {
        OrderDO orderDO = getRequiredOrder(id);
        List<OrderItemDO> items = orderItemMapper.selectList(Wrappers.<OrderItemDO>lambdaQuery()
                .eq(OrderItemDO::getOrderId, id)
                .orderByAsc(OrderItemDO::getId));
        return OrderDetailVO.from(orderDO, items);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long id, OrderStatusRequest request) {
        OrderDO orderDO = getRequiredOrder(id);
        OrderStatus currentStatus = OrderStatus.fromCode(orderDO.getStatus());
        OrderStatus targetStatus = OrderStatus.fromCode(request.getStatus());
        if (currentStatus == targetStatus) {
            return;
        }
        if (!currentStatus.canTransitionTo(targetStatus)) {
            throw new BusinessException(400,
                    "订单状态不能从“" + currentStatus.getDescription()
                            + "”变更为“" + targetStatus.getDescription() + "”");
        }

        LocalDateTime now = LocalDateTime.now();
        OrderDO updateDO = new OrderDO();
        updateDO.setId(id);
        updateDO.setStatus(targetStatus.getCode());
        switch (targetStatus) {
            case PAID -> updateDO.setPayTime(now);
            case SHIPPED -> updateDO.setShipTime(now);
            case COMPLETED -> updateDO.setFinishTime(now);
            case CANCELED -> updateDO.setCancelTime(now);
            default -> {
            }
        }
        // 以读取时的状态作为更新条件，避免并发操作时后提交的请求覆盖先提交的结果
        int updated = orderMapper.update(updateDO, Wrappers.<OrderDO>lambdaUpdate()
                .eq(OrderDO::getId, id)
                .eq(OrderDO::getStatus, currentStatus.getCode()));
        if (updated != 1) {
            throw new BusinessException(409, "订单状态已被其他操作修改，请刷新后重试");
        }
        log.info("修改订单状态成功，订单编号: {}, 状态: {}, 操作人: {}",
                id, targetStatus.getDescription(), SecurityUtils.getCurrentAdminId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteOrder(Long id) {
        OrderDO orderDO = getRequiredOrder(id);
        OrderStatus status = OrderStatus.fromCode(orderDO.getStatus());
        if (status != OrderStatus.COMPLETED && status != OrderStatus.CANCELED) {
            throw new BusinessException(400, "只有已完成或已取消的订单可以删除");
        }
        orderItemMapper.delete(Wrappers.<OrderItemDO>lambdaQuery()
                .eq(OrderItemDO::getOrderId, id));
        if (orderMapper.deleteById(id) != 1) {
            throw new BusinessException(500, "删除订单失败");
        }
        log.info("删除订单成功，订单编号: {}, 操作人: {}",
                id, SecurityUtils.getCurrentAdminId());
    }

    private void validateDateRange(OrderQueryRequest request) {
        if (request.getStartDate() != null && request.getEndDate() != null
                && request.getStartDate().isAfter(request.getEndDate())) {
            throw new BusinessException(400, "开始日期不能晚于结束日期");
        }
    }

    private OrderDO getRequiredOrder(Long id) {
        OrderDO orderDO = orderMapper.selectById(id);
        if (orderDO == null) {
            throw new BusinessException(404, "订单不存在");
        }
        return orderDO;
    }
}
