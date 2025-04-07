package com.ecommerce.adapter.repository;

import com.ecommerce.adapter.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {

    // Top 5 pedidos com pagamento COMPLETED e status PAID
    @Query("SELECT o FROM Order o " +
            "JOIN o.payment p " +
            "WHERE p.status = 'COMPLETED' " +
            "AND o.status = 'PAID' " +
            "ORDER BY o.totalAmount DESC")
    List<Order> findTop5CompletedOrders(Pageable pageable);

    // Ticket médio por usuário considerando pagamentos completos
    @Query("SELECT AVG(o.totalAmount) FROM Order o " +
            "JOIN o.payment p " +
            "WHERE p.status = 'COMPLETED' " +
            "AND o.status = 'PAID' " +
            "AND o.user.id = :userId")
    Double findAverageTicketByUser(@Param("userId") UUID userId);

    // Faturamento mensal considerando pagamentos completos
    @Query("SELECT SUM(o.totalAmount) FROM Order o " +
            "JOIN o.payment p " +
            "WHERE p.status = 'COMPLETED' " +
            "AND o.status = 'PAID' " +
            "AND YEAR(o.createdAt) = YEAR(CURRENT_DATE) " +
            "AND MONTH(o.createdAt) = MONTH(CURRENT_DATE)")
    Double findCurrentMonthRevenue();
}

