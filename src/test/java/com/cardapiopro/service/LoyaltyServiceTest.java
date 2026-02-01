package com.cardapiopro.service;

import com.cardapiopro.dto.request.RedeemPointsRequest;
import com.cardapiopro.dto.response.LoyaltyBalanceResponse;
import com.cardapiopro.dto.response.LoyaltyTransactionResponse;
import com.cardapiopro.entity.Customer;
import com.cardapiopro.entity.LoyaltyTransaction;
import com.cardapiopro.entity.Order;
import com.cardapiopro.entity.enums.LoyaltyTier;
import com.cardapiopro.entity.enums.LoyaltyTransactionType;
import com.cardapiopro.exception.ResourceNotFoundException;
import com.cardapiopro.repository.CustomerRepository;
import com.cardapiopro.repository.LoyaltyTransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("LoyaltyService Tests")
class LoyaltyServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private LoyaltyTransactionRepository transactionRepository;

    @InjectMocks
    private LoyaltyService loyaltyService;

    private Customer testCustomer;
    private UUID customerId;

    @BeforeEach
    void setUp() {
        customerId = UUID.randomUUID();
        testCustomer = Customer.builder()
                .id(customerId)
                .name("Test Customer")
                .email("customer@test.com")
                .loyaltyPoints(500)
                .lifetimePoints(1500)
                .loyaltyTier(LoyaltyTier.SILVER)
                .totalOrders(10)
                .totalSpent(new BigDecimal("1500.00"))
                .build();
    }

    @Test
    @DisplayName("Should get loyalty balance")
    void getBalance_Success() {
        // Arrange
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(testCustomer));

        // Act
        LoyaltyBalanceResponse response = loyaltyService.getBalance(customerId);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.currentPoints()).isEqualTo(500);
        assertThat(response.tier()).isEqualTo(LoyaltyTier.SILVER);
    }

    @Test
    @DisplayName("Should throw exception when customer not found")
    void getBalance_CustomerNotFound() {
        // Arrange
        when(customerRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> loyaltyService.getBalance(UUID.randomUUID()))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Should earn points from order")
    void earnPoints_Success() {
        // Arrange
        Order order = Order.builder()
                .id(UUID.randomUUID())
                .orderNumber("ORD-001")
                .total(new BigDecimal("100.00"))
                .build();

        LoyaltyTransaction transaction = LoyaltyTransaction.builder()
                .id(UUID.randomUUID())
                .customer(testCustomer)
                .transactionType(LoyaltyTransactionType.EARN)
                .points(100)
                .description("Pontos ganhos no pedido #ORD-001")
                .order(order)
                .build();

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(testCustomer));
        when(customerRepository.save(any(Customer.class))).thenReturn(testCustomer);
        when(transactionRepository.save(any(LoyaltyTransaction.class))).thenReturn(transaction);

        // Act
        LoyaltyTransactionResponse response = loyaltyService.earnPoints(customerId, order);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.type()).isEqualTo(LoyaltyTransactionType.EARN);
        assertThat(response.points()).isEqualTo(100);

        verify(customerRepository).save(any(Customer.class));
        verify(transactionRepository).save(any(LoyaltyTransaction.class));
    }

    @Test
    @DisplayName("Should redeem points successfully")
    void redeemPoints_Success() {
        // Arrange
        RedeemPointsRequest request = new RedeemPointsRequest(100, "Resgate de teste");

        LoyaltyTransaction transaction = LoyaltyTransaction.builder()
                .id(UUID.randomUUID())
                .customer(testCustomer)
                .transactionType(LoyaltyTransactionType.REDEEM)
                .points(-100)
                .description("Resgate de teste")
                .build();

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(testCustomer));
        when(customerRepository.save(any(Customer.class))).thenReturn(testCustomer);
        when(transactionRepository.save(any(LoyaltyTransaction.class))).thenReturn(transaction);

        // Act
        LoyaltyTransactionResponse response = loyaltyService.redeemPoints(customerId, request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.type()).isEqualTo(LoyaltyTransactionType.REDEEM);
        assertThat(response.points()).isEqualTo(-100);
    }

    @Test
    @DisplayName("Should throw exception when insufficient points")
    void redeemPoints_InsufficientPoints() {
        // Arrange
        RedeemPointsRequest request = new RedeemPointsRequest(1000, "Resgate grande");

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(testCustomer));

        // Act & Assert
        assertThatThrownBy(() -> loyaltyService.redeemPoints(customerId, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Saldo de pontos insuficiente");
    }

    @Test
    @DisplayName("Should get transaction history")
    void getHistory_Success() {
        // Arrange
        LoyaltyTransaction transaction = LoyaltyTransaction.builder()
                .id(UUID.randomUUID())
                .customer(testCustomer)
                .transactionType(LoyaltyTransactionType.EARN)
                .points(100)
                .description("Pontos ganhos")
                .build();

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(testCustomer));
        when(transactionRepository.findByCustomerIdOrderByCreatedAtDesc(customerId))
                .thenReturn(List.of(transaction));

        // Act
        List<LoyaltyTransactionResponse> history = loyaltyService.getHistory(customerId);

        // Assert
        assertThat(history).hasSize(1);
        assertThat(history.get(0).type()).isEqualTo(LoyaltyTransactionType.EARN);
    }

    @Test
    @DisplayName("Should adjust points (admin)")
    void adjustPoints_Success() {
        // Arrange
        LoyaltyTransaction transaction = LoyaltyTransaction.builder()
                .id(UUID.randomUUID())
                .customer(testCustomer)
                .transactionType(LoyaltyTransactionType.ADJUSTMENT)
                .points(50)
                .description("Ajuste manual")
                .build();

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(testCustomer));
        when(customerRepository.save(any(Customer.class))).thenReturn(testCustomer);
        when(transactionRepository.save(any(LoyaltyTransaction.class))).thenReturn(transaction);

        // Act
        LoyaltyTransactionResponse response = loyaltyService.adjustPoints(customerId, 50, "Ajuste manual");

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.type()).isEqualTo(LoyaltyTransactionType.ADJUSTMENT);
        assertThat(response.points()).isEqualTo(50);
    }
}
