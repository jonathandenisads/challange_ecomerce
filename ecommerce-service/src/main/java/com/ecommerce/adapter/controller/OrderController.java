package com.ecommerce.adapter.controller;

import com.ecommerce.adapter.dto.order.CreateOrderDTO;
import com.ecommerce.adapter.dto.order.OrderResponseDTO;
import com.ecommerce.adapter.model.Order;
import com.ecommerce.adapter.model.Product;
import com.ecommerce.adapter.service.OrderService;
import com.ecommerce.adapter.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {


    private final OrderService orderService;

    private final PaymentService paymentService;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<OrderResponseDTO> createOrder(
            @RequestBody @Valid CreateOrderDTO orderDTO,
            @RequestHeader("Authorization") String token) {

        OrderResponseDTO orderResponseDTO = orderService.createOrder(orderDTO, token);
        return ResponseEntity.status(HttpStatus.CREATED).body(orderResponseDTO);
    }

}
