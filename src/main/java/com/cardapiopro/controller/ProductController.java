package com.cardapiopro.controller;

import com.cardapiopro.dto.request.CreateProductRequest;
import com.cardapiopro.dto.request.UpdateProductRequest;
import com.cardapiopro.dto.response.ErrorResponse;
import com.cardapiopro.dto.response.ProductResponse;
import com.cardapiopro.entity.Product;
import com.cardapiopro.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Tag(name = "Produtos", description = "Gerenciamento de produtos do cardápio")
public class ProductController {

        private final ProductService productService;

        @Operation(summary = "Listar produtos", description = "Retorna todos os produtos ativos e disponíveis")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Lista de produtos retornada com sucesso"),
                        @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
        })
        @GetMapping
        public ResponseEntity<List<ProductResponse>> findAll() {
                List<ProductResponse> products = productService.findAll()
                                .stream()
                                .map(ProductResponse::fromEntity)
                                .toList();
                return ResponseEntity.ok(products);
        }

        @Operation(summary = "Buscar produto por ID", description = "Retorna um produto específico pelo seu ID")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Produto encontrado"),
                        @ApiResponse(responseCode = "400", description = "UUID inválido", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Produto não encontrado", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
        })
        @GetMapping("/{id}")
        public ResponseEntity<ProductResponse> findById(
                        @Parameter(description = "ID do produto (UUID)") @PathVariable UUID id) {
                Product product = productService.findById(id);
                return ResponseEntity.ok(ProductResponse.fromEntity(product));
        }

        @Operation(summary = "Buscar produto por slug", description = "Retorna um produto específico pelo seu slug")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Produto encontrado"),
                        @ApiResponse(responseCode = "404", description = "Produto não encontrado", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
        })
        @GetMapping("/slug/{slug}")
        public ResponseEntity<ProductResponse> findBySlug(
                        @Parameter(description = "Slug do produto (ex: x-burger)") @PathVariable String slug) {
                Product product = productService.findBySlug(slug);
                return ResponseEntity.ok(ProductResponse.fromEntity(product));
        }

        @Operation(summary = "Listar produtos por categoria (ID)", description = "Retorna todos os produtos de uma categoria específica")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Lista de produtos retornada"),
                        @ApiResponse(responseCode = "400", description = "UUID inválido", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
        })
        @GetMapping("/category/{categoryId}")
        public ResponseEntity<List<ProductResponse>> findByCategoryId(
                        @Parameter(description = "ID da categoria (UUID)") @PathVariable UUID categoryId) {
                List<ProductResponse> products = productService.findByCategoryId(categoryId)
                                .stream()
                                .map(ProductResponse::fromEntity)
                                .toList();
                return ResponseEntity.ok(products);
        }

        @Operation(summary = "Listar produtos por categoria (slug)", description = "Retorna todos os produtos de uma categoria pelo slug")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Lista de produtos retornada"),
                        @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
        })
        @GetMapping("/category/slug/{categorySlug}")
        public ResponseEntity<List<ProductResponse>> findByCategorySlug(
                        @Parameter(description = "Slug da categoria (ex: hamburgueres)") @PathVariable String categorySlug) {
                List<ProductResponse> products = productService.findByCategorySlug(categorySlug)
                                .stream()
                                .map(ProductResponse::fromEntity)
                                .toList();
                return ResponseEntity.ok(products);
        }

        @Operation(summary = "Buscar produtos", description = "Busca produtos pelo nome")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Resultado da busca"),
                        @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
        })
        @GetMapping("/search")
        public ResponseEntity<List<ProductResponse>> search(
                        @Parameter(description = "Termo de busca") @RequestParam String q) {
                List<ProductResponse> products = productService.search(q)
                                .stream()
                                .map(ProductResponse::fromEntity)
                                .toList();
                return ResponseEntity.ok(products);
        }

        @Operation(summary = "Criar produto", description = "Cria um novo produto vinculado a uma categoria")
        @ApiResponses({
                        @ApiResponse(responseCode = "201", description = "Produto criado com sucesso"),
                        @ApiResponse(responseCode = "400", description = "Dados inválidos ou slug já existe", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Categoria não encontrada", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
        })
        @PostMapping
        public ResponseEntity<ProductResponse> create(
                        @Valid @RequestBody CreateProductRequest request) {
                Product product = productService.create(request.toEntity(), request.categoryId());
                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(ProductResponse.fromEntity(product));
        }

        @Operation(summary = "Atualizar produto", description = "Atualiza os dados de um produto existente")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Produto atualizado com sucesso"),
                        @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Produto não encontrado", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
        })
        @PutMapping("/{id}")
        public ResponseEntity<ProductResponse> update(
                        @Parameter(description = "ID do produto (UUID)") @PathVariable UUID id,
                        @Valid @RequestBody UpdateProductRequest request) {

                Product updatedData = Product.builder()
                                .name(request.name())
                                .slug(request.slug())
                                .description(request.description())
                                .price(request.price())
                                .promotionalPrice(request.promotionalPrice())
                                .imageUrl(request.imageUrl())
                                .preparationTime(request.preparationTime())
                                .serves(request.serves())
                                .displayOrder(request.displayOrder())
                                .build();

                Product product = productService.update(id, updatedData);
                return ResponseEntity.ok(ProductResponse.fromEntity(product));
        }

        @Operation(summary = "Alterar disponibilidade", description = "Alterna entre disponível/indisponível")
        @ApiResponses({
                        @ApiResponse(responseCode = "204", description = "Disponibilidade alterada"),
                        @ApiResponse(responseCode = "400", description = "UUID inválido", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Produto não encontrado", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
        })
        @PatchMapping("/{id}/availability")
        public ResponseEntity<Void> toggleAvailability(
                        @Parameter(description = "ID do produto (UUID)") @PathVariable UUID id) {
                productService.toggleAvailability(id);
                return ResponseEntity.noContent().build();
        }

        @Operation(summary = "Excluir produto", description = "Remove um produto (soft delete)")
        @ApiResponses({
                        @ApiResponse(responseCode = "204", description = "Produto excluído com sucesso"),
                        @ApiResponse(responseCode = "400", description = "UUID inválido", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Produto não encontrado", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
        })
        @DeleteMapping("/{id}")
        public ResponseEntity<Void> delete(
                        @Parameter(description = "ID do produto (UUID)") @PathVariable UUID id) {
                productService.delete(id);
                return ResponseEntity.noContent().build();
        }
}
