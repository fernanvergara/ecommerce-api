package com.java.demo.ecommerceapi.controllertest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.java.demo.ecommerceapi.model.Brand;
import com.java.demo.ecommerceapi.repository.BrandRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class BrandIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BrandRepository brandRepository;

    private Brand testBrand;

    @BeforeEach
    void setUp() {
        brandRepository.deleteAll();;
        testBrand = new Brand();
        testBrand.setName("Test Brand");
        testBrand = brandRepository.save(testBrand);
    }

    @Test
    @WithMockUser
    void testGetAllBrands() throws Exception {
        mockMvc.perform(get("/api/brands"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Test Brand")));
    }

    @Test
    @WithMockUser
    void testGetBrandById() throws Exception {
        mockMvc.perform(get("/api/brands/"+testBrand.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is("Test Brand")));
    }

    @Test
    @WithMockUser
    void testGetBrandByIdNotFound() throws Exception {
        mockMvc.perform(get("/api/brands/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void testCreateBrand() throws Exception {
        Brand newBrand = new Brand();
        newBrand.setName("New Brand");
        mockMvc.perform(post("/api/brands")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newBrand)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is("New Brand")));
    }

    @Test
    @WithMockUser
    void testUpdateBrand() throws Exception {
        Brand updatedBrand = new Brand();
        updatedBrand.setName("Updated Brand");

        mockMvc.perform(put("/api/brands/"+testBrand.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedBrand)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is("Updated Brand")));
    }

    @Test
    @WithMockUser
    void testUpdateBrandNotFound() throws Exception {
        Brand updatedBrand = new Brand();
        updatedBrand.setName("Updated Brand");
        mockMvc.perform(put("/api/brands/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedBrand)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void testDeleteBrand() throws Exception {
        mockMvc.perform(delete("/api/brands/"+testBrand.getId()))
                .andExpect(status().isOk());
    }

}
