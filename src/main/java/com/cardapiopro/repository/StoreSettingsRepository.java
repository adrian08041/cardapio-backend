package com.cardapiopro.repository;

import com.cardapiopro.entity.StoreSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface StoreSettingsRepository extends JpaRepository<StoreSettings, UUID> {

    Optional<StoreSettings> findFirstByOrderByUpdatedAtDesc();
}
