package com.ecommerce.adapter.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.hibernate.validator.constraints.UUID;

public record CreateOrderItemDTO(
        @NotNull UUID productId,
        @Positive Integer quantity
) {}
