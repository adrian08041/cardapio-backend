package com.cardapiopro.service;

import com.cardapiopro.entity.Category;
import com.cardapiopro.exception.ResourceNotFoundException;
import com.cardapiopro.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CategoryService Tests")
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    private Category testCategory;
    private UUID categoryId;

    @BeforeEach
    void setUp() {
        categoryId = UUID.randomUUID();
        testCategory = Category.builder()
                .id(categoryId)
                .name("Lanches")
                .slug("lanches")
                .description("Lanches deliciosos")
                .active(true)
                .displayOrder(0)
                .build();
    }

    @Test
    @DisplayName("Should find all active categories")
    void findAllActive_Success() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Category> page = new PageImpl<>(List.of(testCategory));

        when(categoryRepository.findByActiveTrue(pageable)).thenReturn(page);

        // Act
        Page<Category> result = categoryService.findAllActive(pageable);

        // Assert
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Lanches");
    }

    @Test
    @DisplayName("Should find category by slug")
    void findBySlug_Success() {
        // Arrange
        when(categoryRepository.findBySlug(anyString())).thenReturn(Optional.of(testCategory));

        // Act
        Category result = categoryService.findBySlug("lanches");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getSlug()).isEqualTo("lanches");
    }

    @Test
    @DisplayName("Should throw exception when category not found by slug")
    void findBySlug_NotFound() {
        // Arrange
        when(categoryRepository.findBySlug(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> categoryService.findBySlug("not-found"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Should create category with generated slug")
    void create_Success() {
        // Arrange
        when(categoryRepository.save(any(Category.class))).thenReturn(testCategory);

        // Act
        Category result = categoryService.create("Lanches", "Lanches deliciosos", null, null, true);

        // Assert
        assertThat(result).isNotNull();
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    @DisplayName("Should delete category by id")
    void delete_Success() {
        // Arrange
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(testCategory));
        doNothing().when(categoryRepository).delete(testCategory);

        // Act
        categoryService.delete(categoryId);

        // Assert
        verify(categoryRepository).delete(testCategory);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent category")
    void delete_NotFound() {
        // Arrange
        when(categoryRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> categoryService.delete(UUID.randomUUID()))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
