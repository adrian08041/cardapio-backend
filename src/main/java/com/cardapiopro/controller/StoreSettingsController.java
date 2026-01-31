package com.cardapiopro.controller;

import com.cardapiopro.dto.request.UpdateStoreSettingsRequest;
import com.cardapiopro.dto.response.StoreSettingsResponse;
import com.cardapiopro.service.StoreSettingsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/settings")
@RequiredArgsConstructor
@Tag(name = "Configurações", description = "Configurações da loja")
public class StoreSettingsController {

    private final StoreSettingsService settingsService;

    @Operation(summary = "Obter configurações", description = "Retorna as configurações atuais da loja")
    @GetMapping
    public ResponseEntity<StoreSettingsResponse> getSettings() {
        return ResponseEntity.ok(StoreSettingsResponse.fromEntity(settingsService.getSettings()));
    }

    @Operation(summary = "Atualizar configurações", description = "Atualiza configurações da loja (Admin)")
    @PutMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<StoreSettingsResponse> updateSettings(@RequestBody UpdateStoreSettingsRequest request) {
        return ResponseEntity.ok(StoreSettingsResponse.fromEntity(settingsService.updateSettings(request)));
    }

    @Operation(summary = "Abrir/Fechar loja", description = "Alterna status de abertura da loja")
    @PatchMapping("/toggle-open")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<StoreSettingsResponse> toggleOpen(@RequestParam boolean open) {
        return ResponseEntity.ok(StoreSettingsResponse.fromEntity(settingsService.toggleOpen(open)));
    }

    @Operation(summary = "Ativar/Desativar delivery", description = "Alterna status do delivery")
    @PatchMapping("/toggle-delivery")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<StoreSettingsResponse> toggleDelivery(@RequestParam boolean enabled) {
        return ResponseEntity.ok(StoreSettingsResponse.fromEntity(settingsService.toggleDelivery(enabled)));
    }

    @Operation(summary = "Ativar/Desativar retirada", description = "Alterna status da retirada")
    @PatchMapping("/toggle-pickup")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<StoreSettingsResponse> togglePickup(@RequestParam boolean enabled) {
        return ResponseEntity.ok(StoreSettingsResponse.fromEntity(settingsService.togglePickup(enabled)));
    }

    @Operation(summary = "Calcular taxa de entrega", description = "Calcula taxa de entrega para um valor de pedido")
    @GetMapping("/delivery-fee")
    public ResponseEntity<BigDecimal> calculateDeliveryFee(@RequestParam BigDecimal orderTotal) {
        return ResponseEntity.ok(settingsService.calculateDeliveryFee(orderTotal));
    }

    @Operation(summary = "Calcular desconto PIX", description = "Calcula desconto PIX para um valor de pedido")
    @GetMapping("/pix-discount")
    public ResponseEntity<BigDecimal> calculatePixDiscount(@RequestParam BigDecimal orderTotal) {
        return ResponseEntity.ok(settingsService.calculatePixDiscount(orderTotal));
    }
}
