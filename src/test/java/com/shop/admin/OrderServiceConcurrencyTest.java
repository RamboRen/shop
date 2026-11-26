package com.shop.admin;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.shop.admin.common.enums.OrderStatus;
import com.shop.admin.common.exception.BusinessException;
import com.shop.admin.order.dto.OrderStatusRequest;
import com.shop.admin.order.entity.OrderDO;
import com.shop.admin.order.mapper.OrderItemMapper;
import com.shop.admin.order.mapper.OrderMapper;
import com.shop.admin.order.service.impl.OrderServiceImpl;
import com.shop.admin.security.AdminPrincipal;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 订单状态并发修改回归测试。
 *
 * @author shop-admin
 */
@ExtendWith(MockitoExtension.class)
class OrderServiceConcurrencyTest {

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private OrderItemMapper orderItemMapper;

    @InjectMocks
    private OrderServiceImpl orderService;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldRejectStaleStatusUpdate() {
        OrderDO orderDO = new OrderDO();
        orderDO.setId(1L);
        orderDO.setStatus(OrderStatus.PENDING_PAYMENT.getCode());
        when(orderMapper.selectById(1L)).thenReturn(orderDO);
        when(orderMapper.update(any(OrderDO.class), any())).thenReturn(0);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> orderService.updateStatus(1L, request(OrderStatus.PAID.getCode())));
        assertEquals(409, exception.getCode());
    }

    @Test
    void shouldUpdateStatusWithExpectedStatusCondition() {
        authenticate();
        OrderDO orderDO = new OrderDO();
        orderDO.setId(2L);
        orderDO.setStatus(OrderStatus.PAID.getCode());
        when(orderMapper.selectById(2L)).thenReturn(orderDO);
        when(orderMapper.update(any(OrderDO.class), any())).thenReturn(1);

        orderService.updateStatus(2L, request(OrderStatus.SHIPPED.getCode()));

        @SuppressWarnings("rawtypes")
        ArgumentCaptor<Wrapper> wrapperCaptor = ArgumentCaptor.forClass(Wrapper.class);
        verify(orderMapper).update(any(OrderDO.class), wrapperCaptor.capture());
        String sqlSegment = wrapperCaptor.getValue().getSqlSegment().replace("`", "");
        assertTrue(sqlSegment.contains("status = #{ew.paramNameValuePairs."), sqlSegment);
    }

    private OrderStatusRequest request(int status) {
        OrderStatusRequest request = new OrderStatusRequest();
        request.setStatus(status);
        return request;
    }

    private void authenticate() {
        AdminPrincipal principal = new AdminPrincipal(1L, "admin", "secret", "管理员",
                "SUPER_ADMIN", true, List.of());
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));
    }
}
