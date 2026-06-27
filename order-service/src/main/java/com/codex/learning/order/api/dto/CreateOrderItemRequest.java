package com.codex.learning.order.api.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record CreateOrderItemRequest(
        @NotNull(message = "productId cannot be null")
        @Min(value = 1, message = "productId must be positive")
        Long productId,

        @NotBlank(message = "productName cannot be blank")
        String productName,

        @NotNull(message = "quantity cannot be null")
        @Min(value = 1, message = "quantity must be positive")
        Integer quantity,

        @NotNull(message = "unitPrice cannot be null")
        @DecimalMin(value = "0.01", message = "unitPrice must be greater than 0")
        BigDecimal unitPrice
) {
}
