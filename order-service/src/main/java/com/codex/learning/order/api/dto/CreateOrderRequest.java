package com.codex.learning.order.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public record CreateOrderRequest(
        @NotNull(message = "userId cannot be null")
        @Min(value = 1, message = "userId must be positive")
        Long userId,

        @Size(max = 200, message = "remark length must be less than 200")
        String remark,

        @Valid
        @NotEmpty(message = "items cannot be empty")
        List<CreateOrderItemRequest> items
) {
}
