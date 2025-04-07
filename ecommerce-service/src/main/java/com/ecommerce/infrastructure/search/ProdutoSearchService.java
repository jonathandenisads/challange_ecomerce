package com.ecommerce.infrastructure.search;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;

import co.elastic.clients.json.JsonData;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;

@Service
public class ProdutoSearchService {

    private final ElasticsearchClient elasticsearchClient;
    private final ElasticsearchOperations elasticsearchOperations;

    public ProdutoSearchService(ElasticsearchClient elasticsearchClient, ElasticsearchOperations elasticsearchOperations) {
        this.elasticsearchClient = elasticsearchClient;
        this.elasticsearchOperations = elasticsearchOperations;
    }

    public void indexProduct(ProductIndex product) throws IOException {
        IndexRequest<ProductIndex> request = IndexRequest.of(i -> i
                .index("products")
                .id(product.getId().toString())
                .document(product)
        );
        IndexResponse response = elasticsearchClient.index(request);
        System.out.println("Resposta do Elasticsearch: " + response.result());
    }

    public List<ProductIndex> searchByName(String nome) throws IOException {
        SearchResponse<ProductIndex> response = elasticsearchClient.search(s -> s
                        .index("products")
                        .query(q -> q
                                .bool(b -> b
                                        .must(m -> m
                                                .match(t -> t
                                                        .field("nome")
                                                        .query(nome)
                                                        .fuzziness("AUTO")  // Tolerância automática a erros
                                                        .prefixLength(2)    // Número mínimo de caracteres corretos no início
                                                        .maxExpansions(50)  // Limite de variações consideradas
                                                )
                                        )
                                        .filter(f -> f
                                                .range(r -> r
                                                        .field("quantidadeEstoque")
                                                        .gt(JsonData.of("0"))  // Apenas produtos com estoque > 0
                                                )
                                        )
                                )
                        ),
                ProductIndex.class
        );

        return response.hits().hits().stream()
                .map(Hit::source)
                .collect(Collectors.toList());
    }

    public List<ProductIndex> searchByCategoria(String categoria) throws IOException {
            SearchResponse<ProductIndex> response = elasticsearchClient.search(s -> s
                            .index("products")
                            .query(q -> q
                                    .bool(b -> b
                                            .must(m -> m
                                                    .match(t -> t
                                                            .field("categoria")
                                                            .query(categoria)
                                                            .fuzziness("AUTO")  // Tolerância automática a erros
                                                            .prefixLength(2)    // Número mínimo de caracteres corretos no início
                                                            .maxExpansions(50)  // Limite de variações consideradas
                                                    )
                                            )
                                            .filter(f -> f
                                                    .range(r -> r
                                                            .field("quantidadeEstoque")
                                                            .gt(JsonData.of("0"))  // Apenas produtos com estoque > 0
                                                    )
                                            )
                                    )
                            ),
                    ProductIndex.class
            );

            return response.hits().hits().stream()
                    .map(Hit::source)
                    .collect(Collectors.toList());
        }

    public void deleteProduct(UUID id) throws IOException {
        elasticsearchClient.delete(d -> d.index("products").id(id.toString()));
    }

    public List<ProductIndex> searchByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) throws IOException {
        // 1. Construa a query de faixa de preço
        var priceRangeQuery = new co.elastic.clients.elasticsearch._types.query_dsl.RangeQuery.Builder()
                .field("preco")
                .gte(JsonData.of(minPrice.doubleValue()))
                .lte(JsonData.of(maxPrice.doubleValue()))
                .build();

        // 2. Construa o filtro de estoque
        var stockFilter = new co.elastic.clients.elasticsearch._types.query_dsl.RangeQuery.Builder()
                .field("quantidadeEstoque")
                .gt(JsonData.of(0))
                .build();

        // 3. Combine ambas as condições usando bool query
        SearchResponse<ProductIndex> response = elasticsearchClient.search(s -> s
                        .index("products")
                        .query(q -> q
                                .bool(b -> b
                                        .must(m -> m.range(priceRangeQuery))  // Faixa de preço obrigatória
                                        .filter(f -> f.range(stockFilter))     // Filtro por estoque
                                )
                        ),
                ProductIndex.class
        );

        return response.hits().hits().stream()
                .map(Hit::source)
                .collect(Collectors.toList());
    }

    public List<ProductIndex> searchAll() throws IOException {
        SearchResponse<ProductIndex> response = elasticsearchClient.search(
                s -> s.index("products")
                        .query(q -> q.matchAll(m -> m)),
                ProductIndex.class
        );

        return response.hits().hits().stream()
                .map(Hit::source)
                .collect(Collectors.toList());
    }


}