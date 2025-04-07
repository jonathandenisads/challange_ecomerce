package com.ecommerce.infrastructure.messaging;


import com.ecommerce.adapter.model.Order;
import com.ecommerce.adapter.model.Product;
import com.ecommerce.adapter.repository.OrderRepository;
import com.ecommerce.adapter.repository.ProductRepository;
import com.ecommerce.infrastructure.search.ProductIndex;
import com.ecommerce.infrastructure.search.ProdutoSearchService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderPaidConsumer {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final ProdutoSearchService produtoSearchService;

    @Transactional
    @KafkaListener(topics = "${app.kafka.topics.order-paid}", groupId = "inventory-group")
    public void handleOrderPaid(@Payload String orderId) {
        UUID uuid = UUID.fromString(orderId);
        Order order = orderRepository.findById(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado: " + orderId));

        order.getItems().forEach(item -> {
            Product product = item.getProduct();
            int newQuantity = product.getQuantidadeEstoque() - item.getQuantity();
            product.setQuantidadeEstoque(newQuantity);

            // Atualiza no MySQL
            productRepository.save(product);

            // Atualiza no Elasticsearch
            ProductIndex productIndex = new ProductIndex(
                    product.getId(),
                    product.getNome(),
                    product.getDescricao(),
                    product.getPreco(),
                    product.getCategoria(),
                    product.getQuantidadeEstoque(),
                    product.getDataCriacao(),
                    product.getDataAtualizacao());

            try {
                produtoSearchService.indexProduct(productIndex);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

}
