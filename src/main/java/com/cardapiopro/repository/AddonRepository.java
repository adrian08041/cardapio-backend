package com.cardapiopro.repository;

import com.cardapiopro.entity.Addon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface AddonRepository extends JpaRepository<Addon, UUID> {

    List<Addon> findByActiveTrueAndAvailableTrueOrderByDisplayOrderAsc();

    List<Addon> findByAddonCategoryIdAndActiveTrueOrderByDisplayOrderAsc(UUID categoryId);
    boolean existsByName(String name);
    long countByAddonCategoryId(UUID categoryId);
}