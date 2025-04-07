package com.ecommerce.adapter.repository;

import com.ecommerce.adapter.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
}
