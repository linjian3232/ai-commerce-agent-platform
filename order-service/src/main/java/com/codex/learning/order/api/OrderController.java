package com.codex.learning.order.api;

import com.codex.learning.order.api.dto.ApiResponse;
import com.codex.learning.order.api.dto.CreateOrderRequest;
import com.codex.learning.order.api.dto.OrderResponse;
import com.codex.learning.order.application.OrderService;
import com.codex.learning.order.domain.OrderStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

import java.util.List;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ApiResponse<OrderResponse> create(@Valid @RequestBody CreateOrderRequest request) {
        return ApiResponse.success(orderService.createOrder(request));
    }

    @GetMapping("/{orderId}")
    public ApiResponse<OrderResponse> getById(@PathVariable @Min(1) Long orderId) {
        return ApiResponse.success(orderService.getOrder(orderId));
    }

    @GetMapping("/users/{userId}")
    public ApiResponse<List<OrderResponse>> listByUserId(@PathVariable @Min(1) Long userId) {
        return ApiResponse.success(orderService.listUserOrders(userId));
    }

    @PostMapping("/{orderId}/pay")
    public ApiResponse<OrderResponse> pay(@PathVariable @Min(1) Long orderId) {
        return ApiResponse.success(orderService.pay(orderId));
    }

    @PostMapping("/{orderId}/cancel")
    public ApiResponse<OrderResponse> cancel(@PathVariable @Min(1) Long orderId) {
        return ApiResponse.success(orderService.cancel(orderId));
    }

    @PostMapping("/{orderId}/ship")
    public ApiResponse<OrderResponse> ship(@PathVariable @Min(1) Long orderId) {
        return ApiResponse.success(orderService.ship(orderId));
    }

    @PostMapping("/{orderId}/complete")
    public ApiResponse<OrderResponse> complete(@PathVariable @Min(1) Long orderId) {
        return ApiResponse.success(orderService.complete(orderId));
    }

    @GetMapping("/users/{userId}/status/{status}")
    public ApiResponse<List<OrderResponse>> listUserOrdersByStatus(@PathVariable Long userId,
                                                                   @PathVariable OrderStatus status) {
        return ApiResponse.success(orderService.listOrdersByStatus(userId, status));
    }
}
