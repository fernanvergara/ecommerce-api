package com.java.demo.ecommerceapi.controllertest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.java.demo.ecommerceapi.config.JwtService;
import com.java.demo.ecommerceapi.controller.CategoryController;
import com.java.demo.ecommerceapi.model.Category;
import com.java.demo.ecommerceapi.service.CategoryService;
import com.java.demo.ecommerceapi.service.IUserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CategoryController.class)
@Import(CategoryIntegrationTest.TestSecurityConfig.class)
class CategoryIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CategoryService categoryService;

    @MockBean
    private IUserService userService;
    
    @MockBean
    private JwtService jwtService;

    private Category testCategory;

    @BeforeEach
    void setUp() {
        testCategory = new Category();
        testCategory.setId(1L);
        testCategory.setName("Test Category");
    }

    @Test
    @WithMockUser
    void testGetAllCategories() throws Exception {
        List<Category> categories = new ArrayList<>();
        categories.add(testCategory);
        when(categoryService.getAllCategories()).thenReturn(categories);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Test Category")));
        verify(categoryService, times(1)).getAllCategories();
    }

    @Test
    @WithMockUser
    void testGetCategoryById() throws Exception {
        when(categoryService.getCategoryById(1L)).thenReturn(Optional.of(testCategory));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/categories/1"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is("Test Category")));
        verify(categoryService, times(1)).getCategoryById(1L);
    }

    @Test
    @WithMockUser
    void testGetCategoryByIdNotFound() throws Exception {
        when(categoryService.getCategoryById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/categories/999"))
                .andExpect(status().isNotFound());
        verify(categoryService, times(1)).getCategoryById(999L);
    }

    @Test
    @WithMockUser
    void testCreateCategory() throws Exception {
        Category newCategory = new Category();
        newCategory.setId(2L);
        newCategory.setName("New Category");
        when(categoryService.createCategory(any(Category.class))).thenReturn(newCategory);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCategory)))
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is("New Category")));
        verify(categoryService, times(1)).createCategory(any(Category.class));
    }

    @Test
    @WithMockUser
    void testUpdateCategory() throws Exception {
        Category updatedCategory = new Category();
        updatedCategory.setId(1L);
        updatedCategory.setName("Updated Category");
        when(categoryService.updateCategory(eq(1L), any(Category.class))).thenReturn(updatedCategory);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/categories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedCategory)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is("Updated Category")));
        verify(categoryService, times(1)).updateCategory(eq(1L), any(Category.class));
    }

    @Test
    @WithMockUser
    void testUpdateCategoryNotFound() throws Exception {
        when(categoryService.updateCategory(eq(999L), any(Category.class))).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/categories/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testCategory)))
                .andExpect(status().isNotFound());
        verify(categoryService, times(1)).updateCategory(eq(999L), any(Category.class));
    }

    @Test
    @WithMockUser
    void testDeleteCategory() throws Exception {
        doNothing().when(categoryService).deleteCategory(1L);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/categories/1"))
                .andExpect(status().isOk());
        verify(categoryService, times(1)).deleteCategory(1L);
    }

    static class TestSecurityConfig {
        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
            http.csrf().disable()
                    .authorizeHttpRequests()
                    .anyRequest().permitAll()
                    .and()
                    .sessionManagement().disable();
            return http.build();
        }
    }
}
