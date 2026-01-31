package com.cardapiopro.service;

import com.cardapiopro.entity.AddonCategory;
import com.cardapiopro.exception.ResourceNotFoundException;
import com.cardapiopro.repository.AddonCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AddonCategoryService {

    private final AddonCategoryRepository repository;

    public List<AddonCategory> findAll() {
        return repository.findByActiveTrueOrderByDisplayOrderAsc();
    }

    public AddonCategory findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria de adicional não encontrada: " + id));
    }

    @Transactional
    public AddonCategory create(AddonCategory category) {
        if (repository.existsByName(category.getName())) {
            throw new IllegalArgumentException("Já existe uma categoria com este nome: " + category.getName());
        }
        return repository.save(category);
    }

    @Transactional
    public AddonCategory update(UUID id, AddonCategory updatedData) {
        AddonCategory category = findById(id);

        if (updatedData.getName() != null) {
            category.setName(updatedData.getName());
        }
        if (updatedData.getDescription() != null) {
            category.setDescription(updatedData.getDescription());
        }
        if (updatedData.getMinSelection() != null) {
            category.setMinSelection(updatedData.getMinSelection());
        }
        if (updatedData.getMaxSelection() != null) {
            category.setMaxSelection(updatedData.getMaxSelection());
        }
        if (updatedData.getDisplayOrder() != null) {
            category.setDisplayOrder(updatedData.getDisplayOrder());
        }

        return repository.save(category);
    }

    @Transactional
    public void delete(UUID id) {
        AddonCategory category = findById(id);
        category.setActive(false);
        repository.save(category);
    }
}