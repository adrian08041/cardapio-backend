package com.cardapiopro.repository;

import com.cardapiopro.entity.LoyaltyTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LoyaltyTransactionRepository extends JpaRepository<LoyaltyTransaction, UUID> {

    List<LoyaltyTransaction> findByCustomerIdOrderByCreatedAtDesc(UUID customerId);

    @Query("SELECT SUM(lt.points) FROM LoyaltyTransaction lt WHERE lt.customer.id = :customerId")
    Integer sumPointsByCustomerId(UUID customerId);
}
