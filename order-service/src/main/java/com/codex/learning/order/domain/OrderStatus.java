package com.codex.learning.order.domain;

import java.util.EnumSet;
import java.util.Set;

public enum OrderStatus {
    CREATED,
    PAID,
    SHIPPED,
    COMPLETED,
    CANCELED;

    public boolean canTransferTo(OrderStatus targetStatus) {
        return allowedTargets().contains(targetStatus);
    }

    private Set<OrderStatus> allowedTargets() {
        return switch (this) {
            case CREATED -> EnumSet.of(PAID, CANCELED);
            case PAID -> EnumSet.of(SHIPPED);
            case SHIPPED -> EnumSet.of(COMPLETED);
            case COMPLETED, CANCELED -> EnumSet.noneOf(OrderStatus.class);
        };
    }
}
