package com.ecommerce.adapter.repository;

import com.ecommerce.adapter.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
}