package com.ecommerce.adapter.dto.report;

public record RevenueReportDTO(
    String message,
    Double revenue,
    String currency,
    String period
) {}
