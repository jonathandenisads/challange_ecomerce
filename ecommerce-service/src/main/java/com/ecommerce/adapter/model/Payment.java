package com.ecommerce.adapter.model;

import com.ecommerce.adapter.enums.PaymentMethod;
import com.ecommerce.adapter.enums.PaymentStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.*;
import com.ecommerce.adapter.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "payments")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor

public class Payment {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod method; // Enum: CREDIT_CARD, PIX, BOLETO

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status = PaymentStatus.PROCESSING; // Enum: PROCESSING, COMPLETED, FAILED

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    // Método para processar pagamento
    public void completePayment() {
        this.status = PaymentStatus.COMPLETED;
        this.paidAt = LocalDateTime.now();
        this.order.setStatus(OrderStatus.PAID);
    }
}