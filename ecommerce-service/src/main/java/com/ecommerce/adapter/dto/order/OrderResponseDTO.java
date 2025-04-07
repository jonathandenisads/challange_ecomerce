package com.ecommerce.adapter.dto.order;

import com.ecommerce.adapter.enums.OrderStatus;
import com.ecommerce.adapter.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record OrderResponseDTO(
        UUID orderId,
        UUID userId,
        OrderStatus status,
        BigDecimal totalAmount,
        LocalDateTime createdAt,
        List<OrderItemResponseDTO> items,
        PaymentStatus paymentStatus
) {}
