package com.ecommerce.adapter.dto;


import org.hibernate.validator.constraints.UUID;

import java.math.BigDecimal;

public record OrderItemResponseDTO(
        UUID productId,
        String productName,
        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal subtotal
) {}