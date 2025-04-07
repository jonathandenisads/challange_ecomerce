package com.ecommerce.adapter.repository;

import com.ecommerce.adapter.model.Payment;
import org.hibernate.validator.constraints.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {
}
