package com.ecommerce.adapter.controller;

import com.ecommerce.adapter.dto.order.CreateOrderDTO;
import com.ecommerce.adapter.dto.order.OrderResponseDTO;
import com.ecommerce.adapter.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {


    private final OrderService orderService;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> createOrder(
            @RequestBody @Valid CreateOrderDTO orderDTO,
            @RequestHeader("Authorization") String token) {
        try {
            OrderResponseDTO response = orderService.createOrder(orderDTO, token);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (ResponseStatusException e) {
            // Trata especificamente erros de negócio (estoque insuficiente)
            if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                return ResponseEntity.badRequest().body(e.getReason());
            }
            return ResponseEntity.internalServerError().body("Erro no servidor");
        }

    }

}
