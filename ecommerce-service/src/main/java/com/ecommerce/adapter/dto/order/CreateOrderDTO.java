package com.ecommerce.adapter.dto;

import com.ecommerce.adapter.enums.PaymentMethod;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.UUID;

import java.util.List;

public record CreateOrderDTO(
        @NotNull UUID userId,
        @NotNull List<@Valid CreateOrderItemDTO> items,
        PaymentMethod paymentMethod
) {}
