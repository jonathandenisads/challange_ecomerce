package com.ecommerce.adapter.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.ecommerce.adapter.model.Product;
import com.ecommerce.infrastructure.search.ProductIndex;
import com.ecommerce.infrastructure.search.ProdutoSearchService;
import com.ecommerce.adapter.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProdutoSearchService productSearchService;


    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public Optional<Product> findById(UUID id) {
        return productRepository.findById(id);
    }

    public List<ProductIndex> findByNameCategoryAndPrice(String nome, String categoria, BigDecimal minPreco, BigDecimal maxPreco) {
        try {
            if (nome != null) {
                return productSearchService.searchByName(nome);
            } else if (categoria != null) {
                return productSearchService.searchByCategoria(categoria);
            } else if (minPreco != null && maxPreco != null) {
                return productSearchService.searchByPriceRange(minPreco, maxPreco);

            } else {
                return productSearchService.searchAll();
            }
        } catch (IOException e) {
            throw new RuntimeException("Erro ao buscar no Elasticsearch", e);
        }
    }

    @Transactional
    public Product save(Product product) {
        Product savedProduct = productRepository.save(product);

        ProductIndex productIndex = new ProductIndex(
                savedProduct.getId(),
                savedProduct.getNome(),
                savedProduct.getDescricao(),
                savedProduct.getPreco(),
                savedProduct.getCategoria(),
                savedProduct.getQuantidadeEstoque(),
                savedProduct.getDataCriacao(),
                savedProduct.getDataAtualizacao()
        );

        try {
            productSearchService.indexProduct(productIndex);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao indexar produto no Elasticsearch", e);
        }

        return savedProduct;
    }

    @Transactional
    public Product updateProduct(UUID id, Product product) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Produto não encontrado.");
        }

        product.setId(id);
        Product updatedProduct = productRepository.save(product);

        ProductIndex productIndex = new ProductIndex(
                updatedProduct.getId(),
                updatedProduct.getNome(),
                updatedProduct.getDescricao(),
                updatedProduct.getPreco(),
                updatedProduct.getCategoria(),
                updatedProduct.getQuantidadeEstoque(),
                updatedProduct.getDataCriacao(),
                updatedProduct.getDataAtualizacao()
        );

        try {
            productSearchService.indexProduct(productIndex);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao atualizar produto no Elasticsearch", e);
        }

        return updatedProduct;
    }

    @Transactional
    public void deleteProduct(UUID id) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado"));

        productRepository.deleteById(existingProduct.getId());

        try {
            productSearchService.deleteProduct(id);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao deletar produto no Elasticsearch", e);
        }
    }

}