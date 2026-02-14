package com.cardapiopro.repository;

import com.cardapiopro.entity.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
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

    @Nested
    @DisplayName("save and findById")
    class SaveAndFindById {

        @Test
        @DisplayName("Deve salvar e encontrar categoria por ID")
        void shouldSaveAndFindCategoryById() {
            Category category = Category.builder()
                    .name("Lanches")
                    .slug("lanches")
                    .icon("🍔")
                    .active(true)
                    .displayOrder(0)
                    .build();

            Category saved = categoryRepository.save(category);
            Optional<Category> found = categoryRepository.findById(saved.getId());

            assertThat(found).isPresent();
            assertThat(found.get().getName()).isEqualTo("Lanches");
            assertThat(found.get().getSlug()).isEqualTo("lanches");
            assertThat(found.get().getId()).isNotNull();
        }

        @Test
        @DisplayName("Deve gerar ID e timestamps automaticamente")
        void shouldGenerateIdAndTimestampsAutomatically() {
            Category category = Category.builder()
                    .name("Bebidas")
                    .slug("bebidas")
                    .icon("🥤")
                    .active(true)
                    .displayOrder(1)
                    .build();

            Category saved = categoryRepository.save(category);

            assertThat(saved.getId()).isNotNull();
            assertThat(saved.getCreatedAt()).isNotNull();
        }
    }

    @Nested
    @DisplayName("findBySlug")
    class FindBySlug {

        @Test
        @DisplayName("Deve encontrar categoria por slug")
        void shouldFindCategoryBySlug() {
            categoryRepository.save(Category.builder()
                    .name("Bebidas")
                    .slug("bebidas")
                    .icon("🥤")
                    .active(true)
                    .displayOrder(0)
                    .build());

            Optional<Category> found = categoryRepository.findBySlug("bebidas");

            assertThat(found).isPresent();
            assertThat(found.get().getName()).isEqualTo("Bebidas");
        }

        @Test
        @DisplayName("Deve retornar vazio para slug inexistente")
        void shouldReturnEmptyForNonExistentSlug() {
            Optional<Category> found = categoryRepository.findBySlug("not-found");

            assertThat(found).isEmpty();
        }

        @Test
        @DisplayName("Slug deve ser case-sensitive")
        void slugShouldBeCaseSensitive() {
            categoryRepository.save(Category.builder()
                    .name("Bebidas")
                    .slug("Bebidas")
                    .icon("🥤")
                    .active(true)
                    .displayOrder(0)
                    .build());

            Optional<Category> foundExact = categoryRepository.findBySlug("Bebidas");
            Optional<Category> foundLower = categoryRepository.findBySlug("bebidas");

            assertThat(foundExact).isPresent();
            assertThat(foundLower).isEmpty();
        }
    }

    @Nested
    @DisplayName("findByActiveTrueOrderByDisplayOrderAsc")
    class FindActiveCategories {

        @Test
        @DisplayName("Deve retornar apenas categorias ativas")
        void shouldReturnOnlyActiveCategories() {
            categoryRepository.save(Category.builder()
                    .name("Active Category")
                    .slug("active")
                    .icon("✅")
                    .active(true)
                    .displayOrder(0)
                    .build());

            categoryRepository.save(Category.builder()
                    .name("Inactive Category")
                    .slug("inactive")
                    .icon("❌")
                    .active(false)
                    .displayOrder(1)
                    .build());

            List<Category> activeCategories = categoryRepository.findByActiveTrueOrderByDisplayOrderAsc();

            assertThat(activeCategories).hasSize(1);
            assertThat(activeCategories.get(0).getName()).isEqualTo("Active Category");
        }

        @Test
        @DisplayName("Deve ordenar por displayOrder")
        void shouldOrderByDisplayOrder() {
            categoryRepository.save(Category.builder()
                    .name("Third")
                    .slug("third")
                    .icon("3️⃣")
                    .active(true)
                    .displayOrder(3)
                    .build());

            categoryRepository.save(Category.builder()
                    .name("First")
                    .slug("first")
                    .icon("1️⃣")
                    .active(true)
                    .displayOrder(1)
                    .build());

            categoryRepository.save(Category.builder()
                    .name("Second")
                    .slug("second")
                    .icon("2️⃣")
                    .active(true)
                    .displayOrder(2)
                    .build());

            List<Category> categories = categoryRepository.findByActiveTrueOrderByDisplayOrderAsc();

            assertThat(categories).hasSize(3);
            assertThat(categories.get(0).getName()).isEqualTo("First");
            assertThat(categories.get(1).getName()).isEqualTo("Second");
            assertThat(categories.get(2).getName()).isEqualTo("Third");
        }

        @Test
        @DisplayName("Deve retornar lista vazia quando não há categorias ativas")
        void shouldReturnEmptyListWhenNoActiveCategories() {
            categoryRepository.save(Category.builder()
                    .name("Inactive")
                    .slug("inactive")
                    .icon("❌")
                    .active(false)
                    .displayOrder(0)
                    .build());

            List<Category> categories = categoryRepository.findByActiveTrueOrderByDisplayOrderAsc();

            assertThat(categories).isEmpty();
        }
    }

    @Nested
    @DisplayName("existsBySlug")
    class ExistsBySlug {

        @Test
        @DisplayName("Deve retornar true quando slug existe")
        void shouldReturnTrueWhenSlugExists() {
            categoryRepository.save(Category.builder()
                    .name("Test Category")
                    .slug("test-slug")
                    .icon("🧪")
                    .active(true)
                    .displayOrder(0)
                    .build());

            assertThat(categoryRepository.existsBySlug("test-slug")).isTrue();
        }

        @Test
        @DisplayName("Deve retornar false quando slug não existe")
        void shouldReturnFalseWhenSlugDoesNotExist() {
            assertThat(categoryRepository.existsBySlug("non-existent")).isFalse();
        }
    }

    @Nested
    @DisplayName("delete")
    class Delete {

        @Test
        @DisplayName("Deve excluir categoria permanentemente")
        void shouldDeleteCategoryPermanently() {
            Category category = categoryRepository.save(Category.builder()
                    .name("To Delete")
                    .slug("to-delete")
                    .icon("🗑️")
                    .active(true)
                    .displayOrder(0)
                    .build());

            categoryRepository.delete(category);

            Optional<Category> found = categoryRepository.findById(category.getId());
            assertThat(found).isEmpty();
        }
    }
}
