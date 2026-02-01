package com.cardapiopro.repository;

import com.cardapiopro.entity.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("CategoryRepository Tests")
class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @BeforeEach
    void setUp() {
        categoryRepository.deleteAll();
    }

    @Test
    @DisplayName("Should save and find category by ID")
    void saveAndFindById() {
        // Arrange
        Category category = Category.builder()
                .name("Lanches")
                .slug("lanches")
                .description("Deliciosos lanches")
                .active(true)
                .displayOrder(0)
                .build();

        // Act
        Category saved = categoryRepository.save(category);
        Optional<Category> found = categoryRepository.findById(saved.getId());

        // Assert
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Lanches");
    }

    @Test
    @DisplayName("Should find category by slug")
    void findBySlug() {
        // Arrange
        categoryRepository.save(Category.builder()
                .name("Bebidas")
                .slug("bebidas")
                .active(true)
                .displayOrder(0)
                .build());

        // Act
        Optional<Category> found = categoryRepository.findBySlug("bebidas");

        // Assert
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Bebidas");
    }

    @Test
    @DisplayName("Should return empty for non-existent slug")
    void findBySlug_NotFound() {
        // Act
        Optional<Category> found = categoryRepository.findBySlug("not-found");

        // Assert
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Should find only active categories")
    void findByActiveTrue() {
        // Arrange
        categoryRepository.save(Category.builder()
                .name("Active Category")
                .slug("active")
                .active(true)
                .displayOrder(0)
                .build());

        categoryRepository.save(Category.builder()
                .name("Inactive Category")
                .slug("inactive")
                .active(false)
                .displayOrder(1)
                .build());

        // Act
        Page<Category> activeCategories = categoryRepository.findByActiveTrue(PageRequest.of(0, 10));

        // Assert
        assertThat(activeCategories.getContent()).hasSize(1);
        assertThat(activeCategories.getContent().get(0).getName()).isEqualTo("Active Category");
    }
}
