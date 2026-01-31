package com.cardapiopro.service;

import com.cardapiopro.dto.request.RedeemPointsRequest;
import com.cardapiopro.dto.response.LoyaltyBalanceResponse;
import com.cardapiopro.dto.response.LoyaltyTransactionResponse;
import com.cardapiopro.entity.Customer;
import com.cardapiopro.entity.LoyaltyTransaction;
import com.cardapiopro.entity.Order;
import com.cardapiopro.entity.enums.LoyaltyTransactionType;
import com.cardapiopro.exception.ResourceNotFoundException;
import com.cardapiopro.repository.CustomerRepository;
import com.cardapiopro.repository.LoyaltyTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LoyaltyService {

    private final CustomerRepository customerRepository;
    private final LoyaltyTransactionRepository transactionRepository;

    public LoyaltyBalanceResponse getBalance(UUID customerId) {
        Customer customer = findCustomerById(customerId);
        return LoyaltyBalanceResponse.fromCustomer(customer);
    }

    public List<LoyaltyTransactionResponse> getHistory(UUID customerId) {
        findCustomerById(customerId);
        return transactionRepository.findByCustomerIdOrderByCreatedAtDesc(customerId)
                .stream()
                .map(LoyaltyTransactionResponse::fromEntity)
                .toList();
    }

    @Transactional
    public LoyaltyTransactionResponse earnPoints(UUID customerId, Order order) {
        Customer customer = findCustomerById(customerId);

        int pointsToEarn = order.getTotal().intValue();

        customer.addLoyaltyPoints(pointsToEarn);
        customerRepository.save(customer);

        LoyaltyTransaction transaction = LoyaltyTransaction.builder()
                .customer(customer)
                .transactionType(LoyaltyTransactionType.EARN)
                .points(pointsToEarn)
                .description("Pontos ganhos no pedido #" + order.getOrderNumber())
                .order(order)
                .build();

        return LoyaltyTransactionResponse.fromEntity(transactionRepository.save(transaction));
    }

    @Transactional
    public LoyaltyTransactionResponse redeemPoints(UUID customerId, RedeemPointsRequest request) {
        Customer customer = findCustomerById(customerId);

        if (customer.getLoyaltyPoints() < request.points()) {
            throw new IllegalArgumentException(
                    "Saldo de pontos insuficiente. Disponível: " + customer.getLoyaltyPoints());
        }

        customer.redeemPoints(request.points());
        customerRepository.save(customer);

        String description = request.description() != null
                ? request.description()
                : "Resgate de " + request.points() + " pontos";

        LoyaltyTransaction transaction = LoyaltyTransaction.builder()
                .customer(customer)
                .transactionType(LoyaltyTransactionType.REDEEM)
                .points(-request.points())
                .description(description)
                .build();

        return LoyaltyTransactionResponse.fromEntity(transactionRepository.save(transaction));
    }

    @Transactional
    public LoyaltyTransactionResponse adjustPoints(UUID customerId, int points, String reason) {
        Customer customer = findCustomerById(customerId);

        if (points > 0) {
            customer.addLoyaltyPoints(points);
        } else {
            customer.redeemPoints(Math.abs(points));
        }
        customerRepository.save(customer);

        LoyaltyTransaction transaction = LoyaltyTransaction.builder()
                .customer(customer)
                .transactionType(LoyaltyTransactionType.ADJUSTMENT)
                .points(points)
                .description(reason)
                .build();

        return LoyaltyTransactionResponse.fromEntity(transactionRepository.save(transaction));
    }

    private Customer findCustomerById(UUID id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado: " + id));
    }
}
