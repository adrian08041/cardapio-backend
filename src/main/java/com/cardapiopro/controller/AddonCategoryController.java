package com.cardapiopro.controller;

import com.cardapiopro.dto.request.CreateAddonCategoryRequest;
import com.cardapiopro.dto.request.UpdateAddonCategoryRequest;
import com.cardapiopro.dto.response.AddonCategoryResponse;
import com.cardapiopro.dto.response.ErrorResponse;
import com.cardapiopro.entity.AddonCategory;
import com.cardapiopro.service.AddonCategoryService;
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
@RequestMapping("/api/v1/addon-categories")
@RequiredArgsConstructor
@Tag(name = "Categorias de Adicionais", description = "Gerenciamento de categorias de adicionais (ex: Queijos, Molhos)")
public class AddonCategoryController {

    private final AddonCategoryService service;

    @Operation(summary = "Listar categorias de adicionais", description = "Retorna todas as categorias ativas")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
            @ApiResponse(responseCode = "500", description = "Erro interno",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping
    public ResponseEntity<List<AddonCategoryResponse>> findAll() {
        List<AddonCategoryResponse> categories = service.findAll()
                .stream()
                .map(AddonCategoryResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(categories);
    }

    @Operation(summary = "Buscar categoria por ID", description = "Retorna uma categoria específica")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Categoria encontrada"),
            @ApiResponse(responseCode = "404", description = "Categoria não encontrada",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<AddonCategoryResponse> findById(
            @Parameter(description = "ID da categoria (UUID)") @PathVariable UUID id) {
        AddonCategory category = service.findById(id);
        return ResponseEntity.ok(AddonCategoryResponse.fromEntity(category));
    }

    @Operation(summary = "Criar categoria de adicional", description = "Cria uma nova categoria")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Categoria criada"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<AddonCategoryResponse> create(
            @Valid @RequestBody CreateAddonCategoryRequest request) {
        AddonCategory category = service.create(request.toEntity());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(AddonCategoryResponse.fromEntity(category));
    }

    @Operation(summary = "Atualizar categoria", description = "Atualiza uma categoria existente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Categoria atualizada"),
            @ApiResponse(responseCode = "404", description = "Categoria não encontrada",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<AddonCategoryResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateAddonCategoryRequest request) {

        AddonCategory updatedData = AddonCategory.builder()
                .name(request.name())
                .description(request.description())
                .minSelection(request.minSelection())
                .maxSelection(request.maxSelection())
                .displayOrder(request.displayOrder())
                .build();

        AddonCategory category = service.update(id, updatedData);
        return ResponseEntity.ok(AddonCategoryResponse.fromEntity(category));
    }

    @Operation(summary = "Excluir categoria", description = "Remove uma categoria (soft delete)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Categoria excluída"),
            @ApiResponse(responseCode = "404", description = "Categoria não encontrada",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}