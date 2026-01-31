package com.cardapiopro.dto.request;

import com.cardapiopro.entity.enums.OrderType;
import com.cardapiopro.entity.enums.PaymentMethod;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record CreateOrderRequest(
                @NotBlank(message = "Nome é obrigatório") @Size(min = 2, max = 100) String customerName,

                @NotBlank(message = "Telefone é obrigatório") @Size(min = 10, max = 20) String customerPhone,

                @Email(message = "Email inválido") String customerEmail,

                String deliveryAddress,
                String deliveryComplement,
                String deliveryNeighborhood,

                @NotNull(message = "Tipo do pedido é obrigatório") OrderType orderType,

                @NotNull(message = "Forma de pagamento é obrigatória") PaymentMethod paymentMethod,

                BigDecimal changeFor,

                BigDecimal deliveryFee,

                // Desconto
                BigDecimal discount,

                // Código do Cupom (opcional)
                String couponCode,

                // Observações
                @Size(max = 500) String notes,

                @NotEmpty(message = "Pedido deve ter pelo menos 1 item") @Valid List<OrderItemRequest> items) {
        public record OrderItemRequest(
                        @NotNull(message = "Produto é obrigatório") UUID productId,

                        @NotNull(message = "Quantidade é obrigatória") @Min(value = 1, message = "Quantidade mínima é 1") Integer quantity,

                        @Size(max = 300) String notes,

                        List<OrderAddonRequest> addons) {
        }

        public record OrderAddonRequest(
                        @NotNull(message = "Addon é obrigatório") UUID addonId,

                        @NotNull @Min(1) Integer quantity) {
        }
}