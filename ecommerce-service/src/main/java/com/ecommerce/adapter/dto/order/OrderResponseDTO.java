package com.ecommerce.adapter.dto;

import com.ecommerce.adapter.enums.OrderStatus;
import com.ecommerce.adapter.enums.PaymentStatus;
import org.hibernate.validator.constraints.UUID;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponseDTO(
        UUID orderId,
        UUID userId,
        OrderStatus status,
        BigDecimal totalAmount,
        LocalDateTime createdAt,
        List<OrderItemResponseDTO> items,
        PaymentStatus paymentStatus
) {}
