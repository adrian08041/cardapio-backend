package com.cardapiopro.controller;

import com.cardapiopro.dto.request.CancelOrderRequest;
import com.cardapiopro.dto.request.CreateOrderRequest;
import com.cardapiopro.dto.request.UpdateOrderStatusRequest;
import com.cardapiopro.dto.response.ErrorResponse;
import com.cardapiopro.dto.response.OrderResponse;
import com.cardapiopro.entity.Order;
import com.cardapiopro.entity.enums.OrderStatus;
import com.cardapiopro.entity.enums.OrderType;
import com.cardapiopro.entity.enums.PaymentStatus;
import com.cardapiopro.service.OrderService;
import com.cardapiopro.service.OrderService.OrderItemRequest;
import com.cardapiopro.service.OrderService.OrderAddonRequest;
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
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Tag(name = "Pedidos", description = "Gerenciamento de pedidos do cardápio")
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "Listar todos os pedidos")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
            @ApiResponse(responseCode = "500", description = "Erro interno",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping
    public ResponseEntity<List<OrderResponse>> findAll() {
        List<OrderResponse> orders = orderService.findAll()
                .stream()
                .map(OrderResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(orders);
    }

    @Operation(summary = "Buscar pedido por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pedido encontrado"),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> findById(
            @Parameter(description = "ID do pedido (UUID)") @PathVariable UUID id) {
        Order order = orderService.findById(id);
        return ResponseEntity.ok(OrderResponse.fromEntity(order));
    }

    @Operation(summary = "Buscar pedido por número")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pedido encontrado"),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/number/{orderNumber}")
    public ResponseEntity<OrderResponse> findByOrderNumber(
            @Parameter(description = "Número do pedido") @PathVariable Integer orderNumber) {
        Order order = orderService.findByOrderNumber(orderNumber);
        return ResponseEntity.ok(OrderResponse.fromEntity(order));
    }

    @Operation(summary = "Listar pedidos por status")
    @GetMapping("/status/{status}")
    public ResponseEntity<List<OrderResponse>> findByStatus(
            @Parameter(description = "Status do pedido") @PathVariable OrderStatus status) {
        List<OrderResponse> orders = orderService.findByStatus(status)
                .stream()
                .map(OrderResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(orders);
    }

    @Operation(summary = "Listar pedidos ativos", description = "Pedidos pendentes, confirmados ou em preparo")
    @GetMapping("/active")
    public ResponseEntity<List<OrderResponse>> findActiveOrders() {
        List<OrderResponse> orders = orderService.findActiveOrders()
                .stream()
                .map(OrderResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(orders);
    }

    @Operation(summary = "Listar pedidos de hoje")
    @GetMapping("/today")
    public ResponseEntity<List<OrderResponse>> findTodayOrders() {
        List<OrderResponse> orders = orderService.findTodayOrders()
                .stream()
                .map(OrderResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(orders);
    }

    @Operation(summary = "Buscar pedidos por telefone do cliente")
    @GetMapping("/customer/{phone}")
    public ResponseEntity<List<OrderResponse>> findByCustomerPhone(
            @Parameter(description = "Telefone do cliente") @PathVariable String phone) {
        List<OrderResponse> orders = orderService.findByCustomerPhone(phone)
                .stream()
                .map(OrderResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(orders);
    }

    @Operation(summary = "Criar novo pedido", description = "Cria um pedido com itens e adicionais")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Pedido criado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Produto ou addon não encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<OrderResponse> create(@Valid @RequestBody CreateOrderRequest request) {
        if (request.orderType() == OrderType.DELIVERY &&
                (request.deliveryAddress() == null || request.deliveryAddress().isBlank())) {
            throw new IllegalArgumentException("Endereço é obrigatório para entrega");
        }

        Order order = Order.builder()
                .customerName(request.customerName())
                .customerPhone(request.customerPhone())
                .customerEmail(request.customerEmail())
                .deliveryAddress(request.deliveryAddress())
                .deliveryComplement(request.deliveryComplement())
                .deliveryNeighborhood(request.deliveryNeighborhood())
                .orderType(request.orderType())
                .paymentMethod(request.paymentMethod())
                .changeFor(request.changeFor())
                .deliveryFee(request.deliveryFee())
                .discount(request.discount())
                .notes(request.notes())
                .build();

        List<OrderItemRequest> items = request.items().stream()
                .map(item -> new OrderItemRequest(
                        item.productId(),
                        item.quantity(),
                        item.notes(),
                        item.addons() != null ?
                                item.addons().stream()
                                        .map(a -> new OrderAddonRequest(a.addonId(), a.quantity()))
                                        .toList()
                                : null
                ))
                .toList();

        Order savedOrder = orderService.create(order, items);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(OrderResponse.fromEntity(savedOrder));
    }

    @Operation(summary = "Atualizar status do pedido")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status atualizado"),
            @ApiResponse(responseCode = "400", description = "Transição de status inválida",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderResponse> updateStatus(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateOrderStatusRequest request) {
        Order order = orderService.updateStatus(id, request.status());
        return ResponseEntity.ok(OrderResponse.fromEntity(order));
    }

    @Operation(summary = "Confirmar pedido")
    @PatchMapping("/{id}/confirm")
    public ResponseEntity<OrderResponse> confirmOrder(@PathVariable UUID id) {
        Order order = orderService.updateStatus(id, OrderStatus.CONFIRMED);
        return ResponseEntity.ok(OrderResponse.fromEntity(order));
    }

    @Operation(summary = "Iniciar preparo")
    @PatchMapping("/{id}/prepare")
    public ResponseEntity<OrderResponse> startPreparing(@PathVariable UUID id) {
        Order order = orderService.updateStatus(id, OrderStatus.PREPARING);
        return ResponseEntity.ok(OrderResponse.fromEntity(order));
    }

    @Operation(summary = "Marcar como pronto")
    @PatchMapping("/{id}/ready")
    public ResponseEntity<OrderResponse> markAsReady(@PathVariable UUID id) {
        Order order = orderService.updateStatus(id, OrderStatus.READY);
        return ResponseEntity.ok(OrderResponse.fromEntity(order));
    }

    @Operation(summary = "Marcar como entregue")
    @PatchMapping("/{id}/deliver")
    public ResponseEntity<OrderResponse> markAsDelivered(@PathVariable UUID id) {
        Order order = orderService.updateStatus(id, OrderStatus.DELIVERED);
        return ResponseEntity.ok(OrderResponse.fromEntity(order));
    }

    @Operation(summary = "Cancelar pedido")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pedido cancelado"),
            @ApiResponse(responseCode = "400", description = "Pedido não pode ser cancelado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<OrderResponse> cancelOrder(
            @PathVariable UUID id,
            @Valid @RequestBody CancelOrderRequest request) {
        Order order = orderService.cancel(id, request.reason());
        return ResponseEntity.ok(OrderResponse.fromEntity(order));
    }

    @Operation(summary = "Marcar como pago")
    @PatchMapping("/{id}/pay")
    public ResponseEntity<OrderResponse> markAsPaid(@PathVariable UUID id) {
        Order order = orderService.updatePaymentStatus(id, PaymentStatus.PAID);
        return ResponseEntity.ok(OrderResponse.fromEntity(order));
    }
}