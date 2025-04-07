package com.ecommerce.adapter.dto.report;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class TopSpenderDTO {
    private UUID userId;
    private String userName;
    private BigDecimal totalSpent;
}