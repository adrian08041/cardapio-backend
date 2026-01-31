package com.cardapiopro.controller;

import com.cardapiopro.dto.request.CreateCouponRequest;
import com.cardapiopro.dto.response.CouponResponse;
import com.cardapiopro.dto.response.ErrorResponse;
import com.cardapiopro.entity.Coupon;
import com.cardapiopro.service.CouponService;
import io.swagger.v3.oas.annotations.Operation;
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
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/coupons")
@RequiredArgsConstructor
@Tag(name = "Cupons", description = "Gerenciamento de cupons de desconto")
public class CouponController {

    private final CouponService couponService;

    @Operation(summary = "Listar cupons")
    @GetMapping
    public ResponseEntity<List<CouponResponse>> findAll() {
        var coupons = couponService.findAll()
                .stream()
                .map(CouponResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(coupons);
    }

    @Operation(summary = "Criar cupom")
    @PostMapping
    public ResponseEntity<CouponResponse> create(@Valid @RequestBody CreateCouponRequest request) {
        Coupon coupon = Coupon.builder()
                .code(request.code().toUpperCase())
                .description(request.description())
                .type(request.type())
                .value(request.value())
                .minOrderValue(request.minOrderValue())
                .maxDiscountValue(request.maxDiscountValue())
                .usageLimit(request.usageLimit())
                .startDate(request.startDate())
                .expirationDate(request.expirationDate())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CouponResponse.fromEntity(couponService.create(coupon)));
    }

    @Operation(summary = "Validar cupom", description = "Verifica se cupom é válido para um valor de pedido")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cupom válido (retorna valor do desconto)"),
            @ApiResponse(responseCode = "404", description = "Cupom inválido",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400", description = "Cupom não aplicável (regras de negócio)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/validate/{code}")
    public ResponseEntity<BigDecimal> validate(
            @PathVariable String code,
            @RequestParam BigDecimal subtotal) {

        BigDecimal discount = couponService.calculateDiscount(code, subtotal);
        return ResponseEntity.ok(discount);
    }

    @Operation(summary = "Deletar cupom")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        couponService.delete(id);
        return ResponseEntity.noContent().build();
    }
}