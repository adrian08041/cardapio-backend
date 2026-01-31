package com.cardapiopro.repository;

import com.cardapiopro.entity.ProductAddonCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProductAddonCategoryRepository extends JpaRepository<ProductAddonCategory, UUID> {

    List<ProductAddonCategory> findByProductIdOrderByDisplayOrderAsc(UUID productId);
    void deleteByProductId(UUID productId);
    boolean existsByProductIdAndAddonCategoryId(UUID productId, UUID addonCategoryId);
}