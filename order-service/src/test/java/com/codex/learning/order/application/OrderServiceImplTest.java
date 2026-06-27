package com.codex.learning.order.application;

import com.codex.learning.order.api.dto.CreateOrderItemRequest;
import com.codex.learning.order.api.dto.CreateOrderRequest;
import com.codex.learning.order.api.dto.OrderResponse;
import com.codex.learning.order.api.error.BusinessException;
import com.codex.learning.order.domain.OrderStatus;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class OrderServiceImplTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void cleanDatabase() {
        jdbcTemplate.update("DELETE FROM order_items");
        jdbcTemplate.update("DELETE FROM orders");
    }

    @Test
    void shouldCreateOrderWithItems() {
        OrderResponse order = orderService.createOrder(new CreateOrderRequest(
                1001L,
                "first order",
                List.of(new CreateOrderItemRequest(2001L, "Java Book", 2, new BigDecimal("39.90")))
        ));

        assertThat(order.id()).isNotNull();
        assertThat(order.status()).isEqualTo(OrderStatus.CREATED);
        assertThat(order.totalAmount()).isEqualByComparingTo("79.80");
        assertThat(order.items()).hasSize(1);
    }

    @Test
    void shouldRejectIllegalStatusTransfer() {
        OrderResponse order = orderService.createOrder(new CreateOrderRequest(
                1002L,
                null,
                List.of(new CreateOrderItemRequest(2002L, "Redis Book", 1, new BigDecimal("59.00")))
        ));

        assertThatThrownBy(() -> orderService.complete(order.id()))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("cannot transfer");
    }

    @Test
    void shouldTransferStatusInValidOrder() {
        OrderResponse order = orderService.createOrder(new CreateOrderRequest(
                1003L,
                null,
                List.of(new CreateOrderItemRequest(2003L, "Kafka Book", 1, new BigDecimal("69.00")))
        ));

        OrderResponse paidOrder = orderService.pay(order.id());
        OrderResponse shippedOrder = orderService.ship(order.id());
        OrderResponse completedOrder = orderService.complete(order.id());

        assertThat(paidOrder.status()).isEqualTo(OrderStatus.PAID);
        assertThat(shippedOrder.status()).isEqualTo(OrderStatus.SHIPPED);
        assertThat(completedOrder.status()).isEqualTo(OrderStatus.COMPLETED);
    }

    @Test
    void shouldRejectPayWhenOrderIsNotCreated() {
        OrderResponse order = orderService.createOrder(new CreateOrderRequest(
                1004L,
                null,
                List.of(new CreateOrderItemRequest(2004L, "MySQL Book", 1, new BigDecimal("79.00")))
        ));

        orderService.pay(order.id());

        assertThatThrownBy(() -> orderService.pay(order.id()))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("cannot transfer");
    }

    @Test
    void shouldOnlyAllowOneSuccessfulPayWhenConcurrentRequestsArrive() throws Exception {
        OrderResponse order = orderService.createOrder(new CreateOrderRequest(
                1006L,
                null,
                List.of(new CreateOrderItemRequest(2006L, "Concurrency Book", 1, new BigDecimal("99.00")))
        ));

        int threadCount = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch readyLatch = new CountDownLatch(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);

        List<Future<Boolean>> futures = java.util.stream.IntStream.range(0, threadCount)
                .mapToObj(i -> executorService.submit(() -> {
                    readyLatch.countDown();
                    startLatch.await();
                    try {
                        orderService.pay(order.id());
                        return true;
                    } catch (BusinessException e) {
                        assertThat(e.getMessage()).contains("cannot transfer");
                        return false;
                    }
                }))
                .toList();

        assertThat(readyLatch.await(3, TimeUnit.SECONDS)).isTrue();
        startLatch.countDown();

        long successCount = 0;
        long rejectedCount = 0;
        for (Future<Boolean> future : futures) {
            if (future.get()) {
                successCount++;
            } else {
                rejectedCount++;
            }
        }

        executorService.shutdown();
        assertThat(executorService.awaitTermination(3, TimeUnit.SECONDS)).isTrue();

        OrderResponse latestOrder = orderService.getOrder(order.id());
        assertThat(successCount).isEqualTo(1);
        assertThat(rejectedCount).isEqualTo(threadCount - 1);
        assertThat(latestOrder.status()).isEqualTo(OrderStatus.PAID);
    }

    @Test
    void shouldChangStatusFromPaidToCancelFail() {
        OrderResponse order = orderService.createOrder(new CreateOrderRequest(
                1003L,
                null,
                List.of(new CreateOrderItemRequest(2003L, "Kafka Book", 1, new BigDecimal("69.00")))
        ));

        orderService.pay(order.id());
        assertThatThrownBy(() -> orderService.cancel(order.id()))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("cannot transfer");
    }

    @Test
    void shouldQuerySpecificUserStatusOrder() {
        OrderResponse order1 = orderService.createOrder(new CreateOrderRequest(
                1001L,
                "first order",
                List.of(new CreateOrderItemRequest(2001L, "Java Book", 2, new BigDecimal("39.90")))
        ));

        OrderResponse order2 = orderService.createOrder(new CreateOrderRequest(
                1002L,
                "second order",
                List.of(new CreateOrderItemRequest(2002L, "Python Book", 2, new BigDecimal("39.90")))
        ));

        orderService.pay(order1.id());
        orderService.pay(order2.id());

        List<OrderResponse> orders = orderService.listOrdersByStatus(1001L, OrderStatus.PAID);

        assertThat(orders).hasSize(1);
        assertThat(orders.get(0).id()).isEqualTo(order1.id());
        assertThat(orders.get(0).userId()).isEqualTo(1001L);
        assertThat(orders.get(0).status()).isEqualTo(OrderStatus.PAID);
    }

    @Test
    void shouldRejectShipWhenOrderIsNotPaid() {
        OrderResponse order = orderService.createOrder(new CreateOrderRequest(
                1005L,
                "first order",
                List.of(new CreateOrderItemRequest(2005L, "Java Book", 2, new BigDecimal("39.90")))
        ));

        assertThatThrownBy(() -> orderService.ship(order.id()))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("cannot transfer");
    }

    @Test
    void shouldRejectCancelWhenOrderIsPaid() {
        OrderResponse order = orderService.createOrder(new CreateOrderRequest(
                1005L,
                "first order",
                List.of(new CreateOrderItemRequest(2005L, "Java Book", 2, new BigDecimal("39.90")))
        ));

        orderService.pay(order.id());

        assertThatThrownBy(() -> orderService.cancel(order.id()))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("cannot transfer");
    }

    @Test
    void shouldReturnCanceledOrderWhenCancelAgain() {
        OrderResponse order = orderService.createOrder(new CreateOrderRequest(
                1007L,
                null,
                List.of(new CreateOrderItemRequest(2007L, "Idempotent Book", 1, new BigDecimal("49.00")))
        ));

        OrderResponse firstCanceledOrder = orderService.cancel(order.id());
        OrderResponse secondCanceledOrder = orderService.cancel(order.id());

        assertThat(firstCanceledOrder.status()).isEqualTo(OrderStatus.CANCELED);
        assertThat(secondCanceledOrder.id()).isEqualTo(order.id());
        assertThat(secondCanceledOrder.status()).isEqualTo(OrderStatus.CANCELED);
    }
}
