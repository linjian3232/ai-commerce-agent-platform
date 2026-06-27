package com.codex.learning.order.application;

import com.codex.learning.order.api.dto.CreateOrderRequest;
import com.codex.learning.order.api.dto.OrderResponse;
import com.codex.learning.order.domain.OrderStatus;

import java.util.List;

public interface OrderService {

    OrderResponse createOrder(CreateOrderRequest request);

    OrderResponse getOrder(Long orderId);

    List<OrderResponse> listUserOrders(Long userId);

    OrderResponse pay(Long orderId);

    OrderResponse cancel(Long orderId);

    OrderResponse ship(Long orderId);

    OrderResponse complete(Long orderId);

    List<OrderResponse> listOrdersByStatus(Long userId, OrderStatus orderStatus);
}
