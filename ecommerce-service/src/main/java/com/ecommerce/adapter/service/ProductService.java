package com.ecommerce.adapter.service;

import com.ecommerce.core.domain.model.Product;
import com.ecommerce.entity.Product;
import com.ecommerce.infrastructure.search.ProductIndex;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.repository.ProductIndexRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductIndexRepository productIndexRepository;

    public List<Product> listarTodos() {
        return productRepository.findAll();
    }

    public Optional<Product> buscarPorId(UUID id) {
        return productRepository.findById(id);
    }

    public List<ProductIndex> buscar(String nome, String categoria, BigDecimal minPreco, BigDecimal maxPreco) {
        if (nome != null) {
            return productIndexRepository.findByNomeContainingIgnoreCase(nome);
        } else if (categoria != null) {
            return productIndexRepository.findByCategoria(categoria);
        } else if (minPreco != null && maxPreco != null) {
            return productIndexRepository.findByPrecoBetween(minPreco, maxPreco);
        }
        return (List<ProductIndex>) productIndexRepository.findAll();
    }

    
    public Product salvar(Product product) {
        Product savedProduct = productRepository.save(product);

          // Indexa o produto no Elasticsearch
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

        productIndexRepository.save(productIndex);
        return savedProduct;
    }

    public Product atualizar(UUID id, Product product) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Produto não encontrado.");
        }

        product.setId(id);
        Product updatedProduct = productRepository.save(product);

         // Atualiza também no Elasticsearch
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
        productIndexRepository.save(productIndex);
        return updatedProduct;
    }

    public void deletar(UUID id) {
        productRepository.deleteById(id);
        productIndexRepository.deleteById(id);
    }
}