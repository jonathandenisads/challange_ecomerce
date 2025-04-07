package com.ecommerce.adapter.controller;

import com.ecommerce.adapter.dto.report.TopSpenderDTO;
import com.ecommerce.adapter.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/top-spenders")
    public ResponseEntity<List<TopSpenderDTO>> getTopSpenders() {
        return ResponseEntity.ok(reportService.getTopSpenders());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/average-ticket/{userId}")
    public ResponseEntity<Double> getAverageTicket(@PathVariable String userId) {
        UUID uuid = UUID.fromString(userId);
        return ResponseEntity.ok(reportService.getAverageTicket(uuid));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/monthly-revenue")
    public ResponseEntity<Double> getMonthlyRevenue() {
        return ResponseEntity.ok(reportService.getMonthlyRevenue());
    }
}
