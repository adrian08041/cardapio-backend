package com.cardapiopro.controller;

import com.cardapiopro.dto.request.CreateCategoryRequest;
import com.cardapiopro.dto.request.UpdateCategoryRequest;
import com.cardapiopro.entity.Category;
import com.cardapiopro.repository.CategoryRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("CategoryController Integration Tests")
class CategoryControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CategoryRepository categoryRepository;

    private Category testCategory;

    @BeforeEach
    void setUp() {
        categoryRepository.deleteAll();
        testCategory = categoryRepository.save(Category.builder()
                .name("Lanches")
                .slug("lanches")
                .icon("🍔")
                .active(true)
                .displayOrder(0)
                .build());
    }

    @Nested
    @DisplayName("GET /api/v1/categories")
    class ListCategories {

        @Test
        @DisplayName("Deve listar categorias ativas (endpoint público)")
        void shouldListActiveCategories() throws Exception {
            mockMvc.perform(get("/api/v1/categories"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].name").value("Lanches"))
                    .andExpect(jsonPath("$[0].slug").value("lanches"))
                    .andExpect(jsonPath("$[0].icon").value("🍔"));
        }

        @Test
        @DisplayName("Não deve listar categorias inativas")
        void shouldNotListInactiveCategories() throws Exception {
            categoryRepository.save(Category.builder()
                    .name("Inativa")
                    .slug("inativa")
                    .icon("❌")
                    .active(false)
                    .displayOrder(1)
                    .build());

            mockMvc.perform(get("/api/v1/categories"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)));
        }

        @Test
        @DisplayName("Deve retornar lista vazia quando não há categorias ativas")
        void shouldReturnEmptyListWhenNoActiveCategories() throws Exception {
            categoryRepository.deleteAll();

            mockMvc.perform(get("/api/v1/categories"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$", hasSize(0)));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/categories/{id}")
    class GetCategoryById {

        @Test
        @DisplayName("Deve encontrar categoria por ID (endpoint público)")
        void shouldFindCategoryById() throws Exception {
            mockMvc.perform(get("/api/v1/categories/" + testCategory.getId()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("Lanches"))
                    .andExpect(jsonPath("$.slug").value("lanches"));
        }

        @Test
        @DisplayName("Deve retornar 404 para categoria inexistente")
        void shouldReturn404ForNonExistentCategory() throws Exception {
            mockMvc.perform(get("/api/v1/categories/00000000-0000-0000-0000-000000000000"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("GET /api/v1/categories/slug/{slug}")
    class GetCategoryBySlug {

        @Test
        @DisplayName("Deve encontrar categoria por slug (endpoint público)")
        void shouldFindCategoryBySlug() throws Exception {
            mockMvc.perform(get("/api/v1/categories/slug/lanches"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("Lanches"))
                    .andExpect(jsonPath("$.slug").value("lanches"));
        }

        @Test
        @DisplayName("Deve retornar 404 para slug inexistente")
        void shouldReturn404ForNonExistentSlug() throws Exception {
            mockMvc.perform(get("/api/v1/categories/slug/not-found"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("POST /api/v1/categories")
    class CreateCategory {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Admin deve conseguir criar categoria")
        void adminShouldCreateCategory() throws Exception {
            CreateCategoryRequest request = new CreateCategoryRequest(
                    "Bebidas", "bebidas", "🥤", 1);

            mockMvc.perform(post("/api/v1/categories")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.name").value("Bebidas"))
                    .andExpect(jsonPath("$.slug").value("bebidas"))
                    .andExpect(jsonPath("$.icon").value("🥤"))
                    .andExpect(jsonPath("$.active").value(true));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Deve retornar erro para slug duplicado")
        void shouldReturnErrorForDuplicateSlug() throws Exception {
            CreateCategoryRequest request = new CreateCategoryRequest(
                    "Lanches Duplicado", "lanches", "🍔", 1);

            mockMvc.perform(post("/api/v1/categories")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @WithMockUser(roles = "CUSTOMER")
        @DisplayName("Customer não deve conseguir criar categoria")
        void customerShouldNotCreateCategory() throws Exception {
            CreateCategoryRequest request = new CreateCategoryRequest(
                    "Bebidas", "bebidas", "🥤", 1);

            mockMvc.perform(post("/api/v1/categories")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("Deve negar criação sem autenticação")
        void shouldDenyCreateWithoutAuthentication() throws Exception {
            CreateCategoryRequest request = new CreateCategoryRequest(
                    "Bebidas", "bebidas", "🥤", 1);

            mockMvc.perform(post("/api/v1/categories")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Deve retornar erro para dados inválidos")
        void shouldReturnErrorForInvalidData() throws Exception {
            CreateCategoryRequest request = new CreateCategoryRequest(
                    "", "", null, null);

            mockMvc.perform(post("/api/v1/categories")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/categories/{id}")
    class UpdateCategory {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Admin deve conseguir atualizar categoria")
        void adminShouldUpdateCategory() throws Exception {
            UpdateCategoryRequest request = new UpdateCategoryRequest(
                    "Super Lanches", null, "🍔🔥", null);

            mockMvc.perform(put("/api/v1/categories/" + testCategory.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("Super Lanches"))
                    .andExpect(jsonPath("$.icon").value("🍔🔥"));
        }

        @Test
        @WithMockUser(roles = "CUSTOMER")
        @DisplayName("Customer não deve conseguir atualizar categoria")
        void customerShouldNotUpdateCategory() throws Exception {
            UpdateCategoryRequest request = new UpdateCategoryRequest(
                    "Hacked", null, null, null);

            mockMvc.perform(put("/api/v1/categories/" + testCategory.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Deve retornar 404 ao atualizar categoria inexistente")
        void shouldReturn404ForNonExistentCategoryUpdate() throws Exception {
            UpdateCategoryRequest request = new UpdateCategoryRequest(
                    "Test", null, null, null);

            mockMvc.perform(put("/api/v1/categories/00000000-0000-0000-0000-000000000000")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("DELETE /api/v1/categories/{id}")
    class DeleteCategory {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Admin deve conseguir excluir categoria (soft delete)")
        void adminShouldDeleteCategory() throws Exception {
            mockMvc.perform(delete("/api/v1/categories/" + testCategory.getId()))
                    .andExpect(status().isNoContent());

            mockMvc.perform(get("/api/v1/categories"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));
        }

        @Test
        @WithMockUser(roles = "CUSTOMER")
        @DisplayName("Customer não deve conseguir excluir categoria")
        void customerShouldNotDeleteCategory() throws Exception {
            mockMvc.perform(delete("/api/v1/categories/" + testCategory.getId()))
                    .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Deve retornar 404 ao excluir categoria inexistente")
        void shouldReturn404ForNonExistentCategoryDelete() throws Exception {
            mockMvc.perform(delete("/api/v1/categories/00000000-0000-0000-0000-000000000000"))
                    .andExpect(status().isNotFound());
        }
    }
}
