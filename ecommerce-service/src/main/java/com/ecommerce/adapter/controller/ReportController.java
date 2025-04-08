package com.ecommerce.adapter.controller;

import com.ecommerce.adapter.dto.report.AverageTicketDTO;
import com.ecommerce.adapter.dto.report.RevenueReportDTO;
import com.ecommerce.adapter.dto.report.TopSpenderDTO;
import com.ecommerce.adapter.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
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
    public ResponseEntity<AverageTicketDTO> getAverageTicket(@PathVariable String userId) {

        Double uuid = reportService.getAverageTicket(UUID.fromString(userId));
        AverageTicketDTO response = new AverageTicketDTO(
                "O ticket medio do funcionario consultado é: ",
                uuid
        );
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/monthly-revenue")
    public ResponseEntity<RevenueReportDTO> getMonthlyRevenue() {
        Double revenue = reportService.getMonthlyRevenue();

        RevenueReportDTO response = new RevenueReportDTO(
                "Faturamento mensal calculado com sucesso",
                revenue,
                "BRL",
                LocalDate.now().getMonth().toString() + "/" + LocalDate.now().getYear()
        );


        return ResponseEntity.ok(response);
    }
}
