package com.cardapiopro.repository;

import com.cardapiopro.entity.AddonCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface AddonCategoryRepository extends JpaRepository<AddonCategory, UUID> {

    List<AddonCategory> findByActiveTrueOrderByDisplayOrderAsc();
    boolean existsByName(String name);
}
