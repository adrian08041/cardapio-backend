package com.cardapiopro.controller;

import com.cardapiopro.dto.request.CreateCategoryRequest;
import com.cardapiopro.dto.request.UpdateCategoryRequest;
import com.cardapiopro.dto.response.CategoryResponse;
import com.cardapiopro.entity.Category;
import com.cardapiopro.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@Tag(name = "Categorias", description = "Gerenciamento de categorias do cardápio")
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(summary = "Listar categorias", description = "Retorna todas as categorias ativas ordenadas por ordem de exibição")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de categorias retornada com sucesso")
    })
    @GetMapping
    public ResponseEntity<List<CategoryResponse>> findAll() {
        List<CategoryResponse> categories = categoryService.findAllActive()
                .stream()
                .map(CategoryResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(categories);
    }

    @Operation(summary = "Buscar categoria por ID", description = "Retorna uma categoria específica pelo seu ID único")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Categoria encontrada"),
            @ApiResponse(responseCode = "404", description = "Categoria não encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> findById(
            @Parameter(description = "ID único da categoria (UUID)") @PathVariable UUID id) {
        Category category = categoryService.findById(id);
        return ResponseEntity.ok(CategoryResponse.fromEntity(category));
    }

    @Operation(summary = "Buscar categoria por slug", description = "Retorna uma categoria específica pelo seu slug (URL amigável)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Categoria encontrada"),
            @ApiResponse(responseCode = "404", description = "Categoria não encontrada")
    })
    @GetMapping("/slug/{slug}")
    public ResponseEntity<CategoryResponse> findBySlug(
            @Parameter(description = "Slug da categoria (ex: hamburgueres, bebidas)") @PathVariable String slug) {
        Category category = categoryService.findBySlug(slug);
        return ResponseEntity.ok(CategoryResponse.fromEntity(category));
    }

    @Operation(summary = "Criar nova categoria", description = "Cria uma nova categoria no cardápio. O slug deve ser único.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Categoria criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou slug já existe")
    })
    @PostMapping
    public ResponseEntity<CategoryResponse> create(
            @Valid @RequestBody CreateCategoryRequest request) {
        Category category = categoryService.create(request.toEntity());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CategoryResponse.fromEntity(category));
    }

    @Operation(summary = "Atualizar categoria", description = "Atualiza os dados de uma categoria existente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Categoria atualizada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Categoria não encontrada"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> update(
            @Parameter(description = "ID único da categoria (UUID)") @PathVariable UUID id,
            @Valid @RequestBody UpdateCategoryRequest request) {

        Category updatedData = Category.builder()
                .name(request.name())
                .slug(request.slug())
                .icon(request.icon())
                .displayOrder(request.displayOrder())
                .build();

        Category category = categoryService.update(id, updatedData);
        return ResponseEntity.ok(CategoryResponse.fromEntity(category));
    }

    @Operation(summary = "Excluir categoria", description = "Remove uma categoria (soft delete - apenas desativa)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Categoria excluída com sucesso"),
            @ApiResponse(responseCode = "404", description = "Categoria não encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID único da categoria (UUID)") @PathVariable UUID id) {
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
