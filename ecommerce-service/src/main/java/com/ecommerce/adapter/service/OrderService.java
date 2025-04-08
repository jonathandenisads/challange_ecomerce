package com.ecommerce.adapter.service;

import com.ecommerce.adapter.dto.order.CreateOrderDTO;
import com.ecommerce.adapter.dto.order.CreateOrderItemDTO;
import com.ecommerce.adapter.dto.order.OrderItemResponseDTO;
import com.ecommerce.adapter.dto.order.OrderResponseDTO;
import com.ecommerce.adapter.enums.OrderStatus;
import com.ecommerce.adapter.model.*;
import com.ecommerce.adapter.repository.OrderRepository;
import com.ecommerce.adapter.repository.ProductRepository;
import com.ecommerce.adapter.repository.UserRepository;
import com.ecommerce.infrastructure.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    @Autowired
    private PaymentService paymentService;
    private final PlatformTransactionManager transactionManager;

    private List<OrderItem> processOrderItems(List<CreateOrderItemDTO> itemsDTO, Order order) {
        List<OrderItem> items = new ArrayList<>();

        for (CreateOrderItemDTO itemDTO : itemsDTO) {
            Product product = productRepository.findById(itemDTO.productId())
                    .orElseThrow(() -> new ResourceNotFoundException("Produto não Encontrado: " + itemDTO.productId()));

            // Verifica estoque
            if (product.getQuantidadeEstoque() < itemDTO.quantity()) {
                order.setStatus(OrderStatus.CANCELED);
                continue;
            }

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(itemDTO.quantity());
            orderItem.setUnitPrice(product.getPreco());
            orderItem.calculateSubtotal();

            items.add(orderItem);
        }

        return items;
    }

    public OrderResponseDTO  mapToOrderResponseDTO(Order order, Payment payment) {
        List<OrderItemResponseDTO> listOrderItemResponseDTO = order.getItems().stream()
                .map(item -> new OrderItemResponseDTO(
                        item.getProduct().getId(),
                        item.getProduct().getNome(),
                        item.getQuantity(),
                        item.getUnitPrice(),
                        item.getSubtotal()
                        )).toList();
        return new OrderResponseDTO(
                order.getId(),
                order.getUser().getId(),
                order.getStatus(),
                order.getTotalAmount(),
                order.getCreatedAt(),
                listOrderItemResponseDTO,
                payment != null ? payment.getStatus() : null


        );

    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Order persistCanceledOrder(Order order) {
        log.info("Iniciando persistência do pedido cancelado: {}", order.getId());

        TransactionStatus status = transactionManager.getTransaction(
                new DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_REQUIRES_NEW));

        try {
            Order savedOrder = orderRepository.save(order);
            transactionManager.commit(status);
            log.info("Pedido cancelado persistido com sucesso: {}", savedOrder.getId());
            return savedOrder;
        } catch (Exception e) {
            transactionManager.rollback(status);
            log.error("Falha crítica ao persistir pedido cancelado", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Falha ao registrar pedido cancelado");
        }
    }

    @Transactional
    public OrderResponseDTO createOrder(CreateOrderDTO orderDTO, String token) {

        //Buscar usuario autenticado atraves do token
        String username = jwtTokenProvider.getUsernameFromToken(token.substring(7));

        //Buscar usuario por username passando usuario Logado
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario não encontrado"));

        //Criar pedido
        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);
        order.setItems(processOrderItems(orderDTO.items(), order));
        order.calculateTotal();


        if (order.getStatus() == OrderStatus.CANCELED) {
            Order canceledOrder = persistCanceledOrder(order);
            log.warn("Pedido cancelado por estoque: {}", canceledOrder.getId());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Estoque insuficiente");
        }


        //salvando pedidos feitos
        Order orderSaved = orderRepository.save(order);

        //Processar Pagamento
        Payment payment = paymentService.processPayment(orderSaved, orderDTO.paymentMethod());

        return mapToOrderResponseDTO(orderSaved, payment);

    }

}
