package com.cardapiopro.service;

import com.cardapiopro.entity.Category;
import com.cardapiopro.entity.Product;
import com.cardapiopro.exception.ResourceNotFoundException;
import com.cardapiopro.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryService categoryService;

    public List<Product> findAll() {
        return productRepository.findByActiveTrueAndAvailableTrueOrderByDisplayOrderAsc();
    }

    public Product findById(UUID id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado: " + id));
    }

    public Product findBySlug(String slug) {
        return productRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado: " + slug));
    }

    public List<Product> findByCategoryId(UUID categoryId) {
        return productRepository.findByCategoryIdAndActiveTrueOrderByDisplayOrderAsc(categoryId);
    }

    public List<Product> findByCategorySlug(String categorySlug) {
        return productRepository.findByCategorySlugAndActiveTrue(categorySlug);
    }

    public List<Product> search(String query) {
        return productRepository.findByNameContainingIgnoreCaseAndActiveTrue(query);
    }

    @Transactional
    public Product create(Product product, UUID categoryId) {
        if (productRepository.existsBySlug(product.getSlug())) {
            throw new IllegalArgumentException("Slug já existe: " + product.getSlug());
        }

        Category category = categoryService.findById(categoryId);
        product.setCategory(category);

        return productRepository.save(product);
    }

    @Transactional
    public Product update(UUID id, Product updatedData) {
        Product product = findById(id);

        if (updatedData.getName() != null) {
            product.setName(updatedData.getName());
        }
        if (updatedData.getSlug() != null) {
            product.setSlug(updatedData.getSlug());
        }
        if (updatedData.getDescription() != null) {
            product.setDescription(updatedData.getDescription());
        }
        if (updatedData.getPrice() != null) {
            product.setPrice(updatedData.getPrice());
        }
        if (updatedData.getPromotionalPrice() != null) {
            product.setPromotionalPrice(updatedData.getPromotionalPrice());
        }
        if (updatedData.getImageUrl() != null) {
            product.setImageUrl(updatedData.getImageUrl());
        }
        if (updatedData.getPreparationTime() != null) {
            product.setPreparationTime(updatedData.getPreparationTime());
        }
        if (updatedData.getServes() != null) {
            product.setServes(updatedData.getServes());
        }
        if (updatedData.getDisplayOrder() != null) {
            product.setDisplayOrder(updatedData.getDisplayOrder());
        }

        return productRepository.save(product);
    }

    @Transactional
    public Product updateCategory(UUID productId, UUID categoryId) {
        Product product = findById(productId);
        Category category = categoryService.findById(categoryId);
        product.setCategory(category);
        return productRepository.save(product);
    }

    @Transactional
    public void toggleAvailability(UUID id) {
        Product product = findById(id);
        product.setAvailable(!product.isAvailable());
        productRepository.save(product);
    }

    @Transactional
    public void delete(UUID id) {
        Product product = findById(id);
        product.setActive(false);
        productRepository.save(product);
    }
}
