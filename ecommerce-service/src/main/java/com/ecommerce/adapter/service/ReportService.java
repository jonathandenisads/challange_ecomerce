package com.ecommerce.adapter.service;


import com.ecommerce.adapter.dto.report.TopSpenderDTO;
import com.ecommerce.adapter.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {


        private final OrderRepository orderRepository;

        // 1. Top 5 usuários que mais gastaram
        public List<TopSpenderDTO> getTopSpenders() {
            return orderRepository.findTop5CompletedOrders(PageRequest.of(0, 5))
                    .stream()
                    .map(order -> new TopSpenderDTO(
                            order.getUser().getId(),
                            order.getUser().getUsername(),
                            order.getTotalAmount()
                    ))
                    .collect(Collectors.toList());
        }

        // 2. Ticket médio por usuário
        public Double getAverageTicket(UUID userId) {
            return orderRepository.findAverageTicketByUser(userId);
        }

        // 3. Faturamento mensal
        public Double getMonthlyRevenue() {
            Double revenue = orderRepository.findCurrentMonthRevenue();
            return revenue != null ? revenue : 0.0;
        }

}
