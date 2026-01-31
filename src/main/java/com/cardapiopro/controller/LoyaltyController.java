package com.cardapiopro.controller;

import com.cardapiopro.dto.request.RedeemPointsRequest;
import com.cardapiopro.dto.response.LoyaltyBalanceResponse;
import com.cardapiopro.dto.response.LoyaltyTransactionResponse;
import com.cardapiopro.service.LoyaltyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/loyalty")
@RequiredArgsConstructor
@Tag(name = "Fidelidade", description = "Programa de pontos e fidelidade")
public class LoyaltyController {

    private final LoyaltyService loyaltyService;

    @Operation(summary = "Consultar saldo de pontos", description = "Retorna saldo atual, tier e progresso do cliente")
    @GetMapping("/balance/{customerId}")
    public ResponseEntity<LoyaltyBalanceResponse> getBalance(@PathVariable UUID customerId) {
        return ResponseEntity.ok(loyaltyService.getBalance(customerId));
    }

    @Operation(summary = "Histórico de transações", description = "Lista todas as transações de pontos do cliente")
    @GetMapping("/history/{customerId}")
    public ResponseEntity<List<LoyaltyTransactionResponse>> getHistory(@PathVariable UUID customerId) {
        return ResponseEntity.ok(loyaltyService.getHistory(customerId));
    }

    @Operation(summary = "Resgatar pontos", description = "Troca pontos por recompensa ou desconto")
    @PostMapping("/redeem/{customerId}")
    public ResponseEntity<LoyaltyTransactionResponse> redeemPoints(
            @PathVariable UUID customerId,
            @Valid @RequestBody RedeemPointsRequest request) {
        return ResponseEntity.ok(loyaltyService.redeemPoints(customerId, request));
    }

    @Operation(summary = "Ajustar pontos (Admin)", description = "Adiciona ou remove pontos manualmente")
    @PostMapping("/adjust/{customerId}")
    public ResponseEntity<LoyaltyTransactionResponse> adjustPoints(
            @PathVariable UUID customerId,
            @RequestParam int points,
            @RequestParam String reason) {
        return ResponseEntity.ok(loyaltyService.adjustPoints(customerId, points, reason));
    }
}
