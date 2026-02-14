package com.cardapiopro.service;

import com.cardapiopro.entity.Category;
import com.cardapiopro.exception.ResourceNotFoundException;
import com.cardapiopro.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
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
                .icon("🍔")
                .active(true)
                .displayOrder(0)
                .build();
    }

    @Nested
    @DisplayName("findAllActive")
    class FindAllActive {

        @Test
        @DisplayName("Deve retornar todas as categorias ativas")
        void shouldReturnAllActiveCategories() {
            when(categoryRepository.findByActiveTrueOrderByDisplayOrderAsc())
                    .thenReturn(List.of(testCategory));

            List<Category> result = categoryService.findAllActive();

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getName()).isEqualTo("Lanches");
            verify(categoryRepository).findByActiveTrueOrderByDisplayOrderAsc();
        }

        @Test
        @DisplayName("Deve retornar lista vazia quando não há categorias ativas")
        void shouldReturnEmptyListWhenNoActiveCategories() {
            when(categoryRepository.findByActiveTrueOrderByDisplayOrderAsc())
                    .thenReturn(Collections.emptyList());

            List<Category> result = categoryService.findAllActive();

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findBySlug")
    class FindBySlug {

        @Test
        @DisplayName("Deve encontrar categoria por slug")
        void shouldFindCategoryBySlug() {
            when(categoryRepository.findBySlug("lanches")).thenReturn(Optional.of(testCategory));

            Category result = categoryService.findBySlug("lanches");

            assertThat(result).isNotNull();
            assertThat(result.getSlug()).isEqualTo("lanches");
        }

        @Test
        @DisplayName("Deve lançar exceção quando categoria não encontrada")
        void shouldThrowExceptionWhenCategoryNotFound() {
            when(categoryRepository.findBySlug(anyString())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> categoryService.findBySlug("not-found"))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("not-found");
        }
    }

    @Nested
    @DisplayName("findById")
    class FindById {

        @Test
        @DisplayName("Deve encontrar categoria por ID")
        void shouldFindCategoryById() {
            when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(testCategory));

            Category result = categoryService.findById(categoryId);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(categoryId);
        }

        @Test
        @DisplayName("Deve lançar exceção quando categoria não encontrada por ID")
        void shouldThrowExceptionWhenCategoryNotFoundById() {
            UUID randomId = UUID.randomUUID();
            when(categoryRepository.findById(randomId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> categoryService.findById(randomId))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("create")
    class Create {

        @Test
        @DisplayName("Deve criar categoria com sucesso")
        void shouldCreateCategorySuccessfully() {
            Category newCategory = Category.builder()
                    .name("Bebidas")
                    .slug("bebidas")
                    .icon("🥤")
                    .active(true)
                    .displayOrder(1)
                    .build();

            when(categoryRepository.existsBySlug("bebidas")).thenReturn(false);
            when(categoryRepository.save(any(Category.class))).thenReturn(newCategory);

            Category result = categoryService.create(newCategory);

            assertThat(result).isNotNull();
            assertThat(result.getName()).isEqualTo("Bebidas");
            verify(categoryRepository).existsBySlug("bebidas");
            verify(categoryRepository).save(newCategory);
        }

        @Test
        @DisplayName("Deve lançar exceção para slug duplicado")
        void shouldThrowExceptionForDuplicateSlug() {
            Category newCategory = Category.builder()
                    .name("Lanches")
                    .slug("lanches")
                    .build();

            when(categoryRepository.existsBySlug("lanches")).thenReturn(true);

            assertThatThrownBy(() -> categoryService.create(newCategory))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Slug já existe");

            verify(categoryRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("update")
    class Update {

        @Test
        @DisplayName("Deve atualizar categoria com sucesso")
        void shouldUpdateCategorySuccessfully() {
            Category updatedData = Category.builder()
                    .name("Super Lanches")
                    .icon("🍔🔥")
                    .build();

            when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(testCategory));
            when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> invocation.getArgument(0));

            Category result = categoryService.update(categoryId, updatedData);

            assertThat(result).isNotNull();
            assertThat(result.getName()).isEqualTo("Super Lanches");
            assertThat(result.getIcon()).isEqualTo("🍔🔥");
            verify(categoryRepository).save(any(Category.class));
        }

        @Test
        @DisplayName("Deve manter valores não informados na atualização")
        void shouldKeepUnchangedValuesOnUpdate() {
            Category updatedData = Category.builder()
                    .name("Super Lanches")
                    .build();

            when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(testCategory));
            when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> invocation.getArgument(0));

            Category result = categoryService.update(categoryId, updatedData);

            assertThat(result.getSlug()).isEqualTo("lanches");
            assertThat(result.getIcon()).isEqualTo("🍔");
        }

        @Test
        @DisplayName("Deve lançar exceção ao atualizar categoria inexistente")
        void shouldThrowExceptionWhenUpdatingNonExistentCategory() {
            UUID randomId = UUID.randomUUID();
            Category updatedData = Category.builder().name("Test").build();

            when(categoryRepository.findById(randomId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> categoryService.update(randomId, updatedData))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("delete")
    class Delete {

        @Test
        @DisplayName("Deve realizar soft delete da categoria")
        void shouldSoftDeleteCategory() {
            when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(testCategory));
            when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> invocation.getArgument(0));

            categoryService.delete(categoryId);

            assertThat(testCategory.isActive()).isFalse();
            verify(categoryRepository).save(testCategory);
        }

        @Test
        @DisplayName("Deve lançar exceção ao excluir categoria inexistente")
        void shouldThrowExceptionWhenDeletingNonExistentCategory() {
            UUID randomId = UUID.randomUUID();
            when(categoryRepository.findById(randomId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> categoryService.delete(randomId))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(categoryRepository, never()).save(any());
        }
    }
}
