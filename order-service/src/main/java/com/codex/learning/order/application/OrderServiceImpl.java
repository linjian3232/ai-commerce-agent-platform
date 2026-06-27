package com.codex.learning.order.application;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.codex.learning.order.api.dto.CreateOrderItemRequest;
import com.codex.learning.order.api.dto.CreateOrderRequest;
import com.codex.learning.order.api.dto.OrderItemResponse;
import com.codex.learning.order.api.dto.OrderResponse;
import com.codex.learning.order.api.error.BusinessException;
import com.codex.learning.order.api.error.ErrorCode;
import com.codex.learning.order.domain.OrderEntity;
import com.codex.learning.order.domain.OrderItemEntity;
import com.codex.learning.order.domain.OrderStatus;
import com.codex.learning.order.infrastructure.mapper.OrderItemMapper;
import com.codex.learning.order.infrastructure.mapper.OrderMapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;

    public OrderServiceImpl(OrderMapper orderMapper, OrderItemMapper orderItemMapper) {
        this.orderMapper = orderMapper;
        this.orderItemMapper = orderItemMapper;
    }

    @Override
    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        LocalDateTime now = LocalDateTime.now();
        BigDecimal totalAmount = request.items()
                .stream()
                .map(this::calculateItemAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        OrderEntity order = new OrderEntity();
        order.setUserId(request.userId());
        order.setTotalAmount(totalAmount);
        order.setStatus(OrderStatus.CREATED);
        order.setRemark(request.remark());
        order.setCreatedAt(now);
        order.setUpdatedAt(now);
        orderMapper.insert(order);

        for (CreateOrderItemRequest itemRequest : request.items()) {
            OrderItemEntity item = new OrderItemEntity();
            item.setOrderId(order.getId());
            item.setProductId(itemRequest.productId());
            item.setProductName(itemRequest.productName());
            item.setQuantity(itemRequest.quantity());
            item.setUnitPrice(itemRequest.unitPrice());
            item.setItemAmount(calculateItemAmount(itemRequest));
            orderItemMapper.insert(item);
        }

        return getOrder(order.getId());
    }

    @Override
    public OrderResponse getOrder(Long orderId) {
        OrderEntity order = findOrder(orderId);
        return toResponse(order, listItems(orderId));
    }

    @Override
    public List<OrderResponse> listUserOrders(Long userId) {
        List<OrderEntity> orders = orderMapper.selectList(new LambdaQueryWrapper<OrderEntity>()
                .eq(OrderEntity::getUserId, userId)
                .orderByDesc(OrderEntity::getCreatedAt));
        return toResponsesWithItems(orders);
    }

    @Override
    @Transactional
    public OrderResponse pay(Long orderId) {
        return changeStatusAtomically(
                orderId,
                OrderStatus.CREATED,
                OrderStatus.PAID,
                IdempotencyPolicy.REJECT_ON_TARGET_STATUS
        );
    }

    @Override
    @Transactional
    public OrderResponse cancel(Long orderId) {
        return changeStatusAtomically(
                orderId,
                OrderStatus.CREATED,
                OrderStatus.CANCELED,
                IdempotencyPolicy.RETURN_ON_TARGET_STATUS
        );
    }

    @Override
    @Transactional
    public OrderResponse ship(Long orderId) {
        return changeStatusAtomically(
                orderId,
                OrderStatus.PAID,
                OrderStatus.SHIPPED,
                IdempotencyPolicy.REJECT_ON_TARGET_STATUS
        );
    }

    @Override
    @Transactional
    public OrderResponse complete(Long orderId) {
        return changeStatus(orderId, OrderStatus.COMPLETED);
    }

    @Override
    public List<OrderResponse> listOrdersByStatus(Long userId, OrderStatus orderStatus) {
        List<OrderEntity> orders = orderMapper.selectList(new LambdaQueryWrapper<OrderEntity>()
                .eq(OrderEntity::getStatus, orderStatus)
                .eq(OrderEntity::getUserId, userId)
                .orderByDesc(OrderEntity::getCreatedAt));
        return toResponsesWithItems(orders);
    }

    private List<OrderResponse> toResponsesWithItems(List<OrderEntity> orders) {
        if (orders.isEmpty()) {
            return List.of();
        }

        List<Long> orderIds = orders.stream()
                .map(OrderEntity::getId)
                .toList();
        Map<Long, List<OrderItemEntity>> itemsByOrderId = orderItemMapper.selectList(
                        new LambdaQueryWrapper<OrderItemEntity>()
                                .in(OrderItemEntity::getOrderId, orderIds)
                                .orderByAsc(OrderItemEntity::getId)
                )
                .stream()
                .collect(Collectors.groupingBy(OrderItemEntity::getOrderId));

        return orders.stream()
                .map(order -> toResponse(order, itemsByOrderId.getOrDefault(order.getId(), List.of())))
                .toList();
    }

    private OrderResponse changeStatusAtomically(Long orderId,
                                                 OrderStatus currentStatus,
                                                 OrderStatus targetStatus,
                                                 IdempotencyPolicy idempotencyPolicy) {
        int updatedRows = orderMapper.updateStatusByIdAndStatus(
                orderId,
                currentStatus,
                targetStatus,
                LocalDateTime.now()
        );
        if (updatedRows == 1) {
            return getOrder(orderId);
        }

        OrderEntity order = findOrder(orderId);
        if (idempotencyPolicy == IdempotencyPolicy.RETURN_ON_TARGET_STATUS
                && order.getStatus() == targetStatus) {
            return toResponse(order, listItems(orderId));
        }

        throw new BusinessException(
                ErrorCode.ORDER_STATUS_ILLEGAL,
                "order status cannot transfer from " + order.getStatus() + " to " + targetStatus
        );
    }

    private enum IdempotencyPolicy {
        REJECT_ON_TARGET_STATUS,
        RETURN_ON_TARGET_STATUS
    }

    private OrderResponse changeStatus(Long orderId, OrderStatus targetStatus) {
        OrderEntity order = findOrder(orderId);
        if (!order.getStatus().canTransferTo(targetStatus)) {
            throw new BusinessException(
                    ErrorCode.ORDER_STATUS_ILLEGAL,
                    "order status cannot transfer from " + order.getStatus() + " to " + targetStatus
            );
        }
        order.setStatus(targetStatus);
        order.setUpdatedAt(LocalDateTime.now());
        orderMapper.updateById(order);
        return getOrder(orderId);
    }

    private OrderEntity findOrder(Long orderId) {
        OrderEntity order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(ErrorCode.ORDER_NOT_FOUND, "order not found: " + orderId);
        }
        return order;
    }

    private List<OrderItemEntity> listItems(Long orderId) {
        return orderItemMapper.selectList(new LambdaQueryWrapper<OrderItemEntity>()
                .eq(OrderItemEntity::getOrderId, orderId)
                .orderByAsc(OrderItemEntity::getId));
    }

    private BigDecimal calculateItemAmount(CreateOrderItemRequest itemRequest) {
        return itemRequest.unitPrice().multiply(BigDecimal.valueOf(itemRequest.quantity()));
    }

    private OrderResponse toResponse(OrderEntity order, List<OrderItemEntity> items) {
        return new OrderResponse(
                order.getId(),
                order.getUserId(),
                order.getTotalAmount(),
                order.getStatus(),
                order.getRemark(),
                order.getCreatedAt(),
                order.getUpdatedAt(),
                items.stream()
                        .map(this::toItemResponse)
                        .toList()
        );
    }

    private OrderItemResponse toItemResponse(OrderItemEntity item) {
        return new OrderItemResponse(
                item.getId(),
                item.getProductId(),
                item.getProductName(),
                item.getQuantity(),
                item.getUnitPrice(),
                item.getItemAmount()
        );
    }


}
