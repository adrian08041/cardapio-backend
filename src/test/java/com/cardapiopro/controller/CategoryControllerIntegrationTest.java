package com.cardapiopro.controller;

import com.cardapiopro.entity.Category;
import com.cardapiopro.repository.CategoryRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

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
                .description("Deliciosos lanches")
                .active(true)
                .displayOrder(0)
                .build());
    }

    @Test
    @DisplayName("Should list all categories (public)")
    void findAll_Success() throws Exception {
        mockMvc.perform(get("/api/v1/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].name").value("Lanches"));
    }

    @Test
    @DisplayName("Should find category by slug (public)")
    void findBySlug_Success() throws Exception {
        mockMvc.perform(get("/api/v1/categories/lanches"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Lanches"))
                .andExpect(jsonPath("$.slug").value("lanches"));
    }

    @Test
    @DisplayName("Should return 404 for non-existent category")
    void findBySlug_NotFound() throws Exception {
        mockMvc.perform(get("/api/v1/categories/not-found"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should create category (admin)")
    void create_Success() throws Exception {
        Map<String, Object> request = Map.of(
                "name", "Bebidas",
                "description", "Bebidas geladas");

        mockMvc.perform(post("/api/v1/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Bebidas"))
                .andExpect(jsonPath("$.slug").value("bebidas"));
    }

    @Test
    @DisplayName("Should deny create without auth")
    void create_Unauthorized() throws Exception {
        Map<String, Object> request = Map.of(
                "name", "Bebidas",
                "description", "Bebidas geladas");

        mockMvc.perform(post("/api/v1/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    @DisplayName("Should deny create for customer role")
    void create_Forbidden() throws Exception {
        Map<String, Object> request = Map.of(
                "name", "Bebidas",
                "description", "Bebidas geladas");

        mockMvc.perform(post("/api/v1/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should update category (admin)")
    void update_Success() throws Exception {
        Map<String, Object> request = Map.of(
                "name", "Super Lanches",
                "description", "Lanches incríveis");

        mockMvc.perform(put("/api/v1/categories/" + testCategory.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Super Lanches"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should delete category (admin)")
    void delete_Success() throws Exception {
        mockMvc.perform(delete("/api/v1/categories/" + testCategory.getId()))
                .andExpect(status().isNoContent());
    }
}
