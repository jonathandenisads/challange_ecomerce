package com.ecommerce.adapter.controller;


import com.ecommerce.adapter.service.ProductService;
import com.ecommerce.adapter.model.Product;
import com.ecommerce.infrastructure.search.ProductIndex;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
@SecurityRequirement(name = "bearerAuth")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/search")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(
            summary = "Busca de Produtos Personalizada",
            description = "Usuários e administradores podem ver os produtos")
    public ResponseEntity<List<ProductIndex>> buscarProdutos(
        @RequestParam(required = false) String nome,
        @RequestParam(required = false) String categoria,
        @RequestParam(required = false) BigDecimal minPreco,
        @RequestParam(required = false) BigDecimal maxPreco) {
    
        return ResponseEntity.ok(productService.buscar(nome, categoria, minPreco, maxPreco));
    }

    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(
            summary = "Listar todos os produtos",
            description = "Acesso permitido para ADMIN e USER"
    )
    public ResponseEntity<List<Product>> listarTodos() {
        return ResponseEntity.ok(productService.listarTodos());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(
            summary = "Buscar produto por ID",
            description = "Consulta detalhes de um produto específico. Acesso para ADMIN e USER"
    )
    public ResponseEntity<Product> buscarPorId(@PathVariable UUID id) {
        Optional<Product> product = productService.buscarPorId(id);
        return product.map(ResponseEntity::ok)
                      .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Criar produto",
            description = "Requer token JWT de um usuário com papel ADMIN"
    )
    public ResponseEntity<Product> criarProduto(@RequestBody Product product) {
        return ResponseEntity.ok(productService.salvar(product));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Atualizar produto",
            description = "Atualiza os dados de um produto existente. Requer papel ADMIN"
    )
    public ResponseEntity<Product> atualizarProduto(@PathVariable UUID id, @RequestBody Product product) {
        return ResponseEntity.ok(productService.atualizar(id, product));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Excluir produto",
            description = "Remove um produto do sistema. Apenas ADMIN pode executar"
    )
    public ResponseEntity<Void> deletarProduto(@PathVariable UUID id) {
        productService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
