package com.cardapiopro.controller;

import com.cardapiopro.dto.request.CreateAddonRequest;
import com.cardapiopro.dto.request.UpdateAddonRequest;
import com.cardapiopro.dto.response.AddonResponse;
import com.cardapiopro.dto.response.ErrorResponse;
import com.cardapiopro.entity.Addon;
import com.cardapiopro.service.AddonService;
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
@RequestMapping("/api/v1/addons")
@RequiredArgsConstructor
@Tag(name = "Adicionais", description = "Gerenciamento de adicionais (ex: Queijo Extra, Bacon)")
public class AddonController {

    private final AddonService service;

    @Operation(summary = "Listar adicionais", description = "Retorna todos os adicionais ativos e disponíveis")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
            @ApiResponse(responseCode = "500", description = "Erro interno",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping
    public ResponseEntity<List<AddonResponse>> findAll() {
        List<AddonResponse> addons = service.findAll()
                .stream()
                .map(AddonResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(addons);
    }

    @Operation(summary = "Buscar adicional por ID", description = "Retorna um adicional específico")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Adicional encontrado"),
            @ApiResponse(responseCode = "404", description = "Adicional não encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<AddonResponse> findById(
            @Parameter(description = "ID do adicional (UUID)") @PathVariable UUID id) {
        Addon addon = service.findById(id);
        return ResponseEntity.ok(AddonResponse.fromEntity(addon));
    }

    @Operation(summary = "Listar adicionais por categoria", description = "Retorna adicionais de uma categoria")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada"),
            @ApiResponse(responseCode = "400", description = "UUID inválido",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<AddonResponse>> findByCategoryId(
            @Parameter(description = "ID da categoria (UUID)") @PathVariable UUID categoryId) {
        List<AddonResponse> addons = service.findByCategoryId(categoryId)
                .stream()
                .map(AddonResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(addons);
    }

    @Operation(summary = "Criar adicional", description = "Cria um novo adicional vinculado a uma categoria")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Adicional criado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Categoria não encontrada",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<AddonResponse> create(
            @Valid @RequestBody CreateAddonRequest request) {
        Addon addon = service.create(request.toEntity(), request.addonCategoryId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(AddonResponse.fromEntity(addon));
    }

    @Operation(summary = "Atualizar adicional", description = "Atualiza um adicional existente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Adicional atualizado"),
            @ApiResponse(responseCode = "404", description = "Adicional não encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<AddonResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateAddonRequest request) {

        Addon updatedData = Addon.builder()
                .name(request.name())
                .description(request.description())
                .price(request.price())
                .maxQuantity(request.maxQuantity())
                .displayOrder(request.displayOrder())
                .build();

        Addon addon = service.update(id, updatedData);
        return ResponseEntity.ok(AddonResponse.fromEntity(addon));
    }

    @Operation(summary = "Alterar disponibilidade", description = "Alterna disponível/indisponível")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Disponibilidade alterada"),
            @ApiResponse(responseCode = "404", description = "Adicional não encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PatchMapping("/{id}/availability")
    public ResponseEntity<Void> toggleAvailability(@PathVariable UUID id) {
        service.toggleAvailability(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Excluir adicional", description = "Remove um adicional (soft delete)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Adicional excluído"),
            @ApiResponse(responseCode = "404", description = "Adicional não encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}