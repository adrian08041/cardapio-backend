package com.cardapiopro.repository;

import com.cardapiopro.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {

    Optional<Product> findBySlug(String slug);
    List<Product> findByActiveTrueAndAvailableTrueOrderByDisplayOrderAsc();
    List<Product> findByCategoryIdAndActiveTrueOrderByDisplayOrderAsc(UUID categoryId);
    @Query("SELECT p FROM Product p WHERE p.category.slug = :categorySlug AND p.active = true ORDER BY p.displayOrder")
    List<Product> findByCategorySlugAndActiveTrue(@Param("categorySlug") String categorySlug);
    List<Product> findByNameContainingIgnoreCaseAndActiveTrue(String name);
    boolean existsBySlug(String slug);
    long countByCategoryId(UUID categoryId);
}