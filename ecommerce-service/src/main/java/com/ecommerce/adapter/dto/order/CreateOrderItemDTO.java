package com.ecommerce.adapter.dto.order;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;


public record CreateOrderItemDTO(
        @NotNull(message = "ID do produto é obrigatório")
        @NotNull UUID productId,

        @NotNull(message = "Quantidade é obrigatória")
        @Positive(message = "Quantidade deve ser maior que zero")
        Integer quantity
) {}
