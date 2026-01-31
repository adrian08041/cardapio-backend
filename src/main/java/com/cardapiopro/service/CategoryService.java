package com.cardapiopro.service;

import com.cardapiopro.entity.Category;
import com.cardapiopro.exception.ResourceNotFoundException;
import com.cardapiopro.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<Category> findAllActive() {
        return categoryRepository.findByActiveTrueOrderByDisplayOrderAsc();
    }

    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    public Category findById(UUID id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada: " + id));
    }

    public Category findBySlug(String slug) {
        return categoryRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada: " + slug));
    }

    @Transactional
    public Category create(Category category) {
        if (categoryRepository.existsBySlug(category.getSlug())) {
            throw new IllegalArgumentException("Slug já existe: " + category.getSlug());
        }
        return categoryRepository.save(category);
    }

    @Transactional
    public Category update(UUID id, Category updatedData) {
        Category category = findById(id);

        if (updatedData.getName() != null) {
            category.setName(updatedData.getName());
        }
        if (updatedData.getSlug() != null) {
            category.setSlug(updatedData.getSlug());
        }
        if (updatedData.getIcon() != null) {
            category.setIcon(updatedData.getIcon());
        }
        if (updatedData.getDisplayOrder() != null) {
            category.setDisplayOrder(updatedData.getDisplayOrder());
        }

        return categoryRepository.save(category);
    }

    @Transactional
    public void delete(UUID id) {
        Category category = findById(id);
        category.setActive(false);
        categoryRepository.save(category);
    }
}
