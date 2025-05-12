package com.java.demo.ecommerceapi.controllertest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.java.demo.ecommerceapi.model.Brand;
import com.java.demo.ecommerceapi.config.JwtService;
import com.java.demo.ecommerceapi.controller.BrandController;
import com.java.demo.ecommerceapi.service.BrandService;
import com.java.demo.ecommerceapi.service.IUserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest(BrandController.class)
@Import(BrandIntegrationTest.TestSecurityConfig.class) 
class BrandIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IUserService userService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private BrandService brandService;

    private Brand testBrand;

    @BeforeEach
    void setUp() {
        testBrand = new Brand();
        testBrand.setId(1L);
        testBrand.setName("Test Brand");
    }

    @Test
    @WithMockUser
    void testGetAllBrands() throws Exception {
        List<Brand> brands = new ArrayList<>();
        brands.add(testBrand);
        when(brandService.getAllBrands()).thenReturn(brands);

        mockMvc.perform(get("/api/brands"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Test Brand")));
        verify(brandService, times(1)).getAllBrands();
    }

    @Test
    @WithMockUser
    void testGetBrandById() throws Exception {
        when(brandService.getBrandById(1L)).thenReturn(Optional.of(testBrand));

        mockMvc.perform(get("/api/brands/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is("Test Brand")));
        verify(brandService, times(1)).getBrandById(1L);
    }

    @Test
    @WithMockUser
    void testGetBrandByIdNotFound() throws Exception {
        when(brandService.getBrandById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/brands/999"))
                .andExpect(status().isNotFound());
        verify(brandService, times(1)).getBrandById(999L);
    }

    @Test
    @WithMockUser
    void testCreateBrand() throws Exception {
        Brand newBrand = new Brand();
        newBrand.setId(2L);
        newBrand.setName("New Brand");
        when(brandService.createBrand(any(Brand.class))).thenReturn(newBrand);

        mockMvc.perform(post("/api/brands")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newBrand)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is("New Brand")));
        verify(brandService, times(1)).createBrand(any(Brand.class));
    }

    @Test
    @WithMockUser
    void testUpdateBrand() throws Exception {
        Brand updatedBrand = new Brand();
        updatedBrand.setId(1L);
        updatedBrand.setName("Updated Brand");

        when(brandService.updateBrand(eq(1L), any(Brand.class))).thenReturn(updatedBrand);

        mockMvc.perform(put("/api/brands/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedBrand)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is("Updated Brand")));
        verify(brandService, times(1)).updateBrand(eq(1L), any(Brand.class));
    }

    @Test
    @WithMockUser
    void testUpdateBrandNotFound() throws Exception {
       when(brandService.updateBrand(eq(999L), any(Brand.class))).thenReturn(null);

        mockMvc.perform(put("/api/brands/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testBrand)))
                .andExpect(status().isNotFound());
        verify(brandService, times(1)).updateBrand(eq(999L), any(Brand.class));
    }

    @Test
    @WithMockUser
    void testDeleteBrand() throws Exception {
        when(brandService.deleteBrand(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/brands/1"))
                .andExpect(status().isNoContent());
        verify(brandService, times(1)).deleteBrand(1L);
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
