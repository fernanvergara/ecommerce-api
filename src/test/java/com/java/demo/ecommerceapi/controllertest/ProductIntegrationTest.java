package com.java.demo.ecommerceapi.controllertest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.java.demo.ecommerceapi.model.Product;
import com.java.demo.ecommerceapi.dto.ProductDTO;
import com.java.demo.ecommerceapi.model.Brand;
import com.java.demo.ecommerceapi.model.Category;
import com.java.demo.ecommerceapi.repository.ProductRepository;
import com.java.demo.ecommerceapi.repository.BrandRepository;
import com.java.demo.ecommerceapi.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest 
@AutoConfigureMockMvc
@Transactional
public class ProductIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private Brand testBrand;
    private Category testCategory;
    private Product product1;

    @BeforeEach
    void setUp() {
        brandRepository.deleteAll();
        categoryRepository.deleteAll();
        productRepository.deleteAll();

        testBrand = new Brand();
        testBrand.setName("Test Brand");
        testBrand = brandRepository.save(testBrand);
 
        testCategory = new Category();
        testCategory.setName("Test Category");
        testCategory = categoryRepository.save(testCategory);

        product1 = new Product();
        product1.setName("Product 1");
        product1.setDescription("Description 1");
        product1.setPrice(new BigDecimal("10.00"));
        product1.setBrand(testBrand);
        product1.setCategory(testCategory);
        product1 = productRepository.save(product1);

}

    @Test
    void testGetAllProducts() throws Exception { 
        Product product2 = new Product();
        product2.setName("Product 2");
        product2.setDescription("Description 2");
        product2.setPrice(new BigDecimal("20.00"));
        product2.setBrand(testBrand);
        product2.setCategory(testCategory);
        productRepository.save(product2);
 
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("Product 1")))
                .andExpect(jsonPath("$[0].description", is("Description 1")))
                .andExpect(jsonPath("$[0].price", is(10.0)))
                .andExpect(jsonPath("$[1].name", is("Product 2")))
                .andExpect(jsonPath("$[1].description", is("Description 2")))
                .andExpect(jsonPath("$[1].price", is(20.0)));
    }

    @Test
    void testGetProductById() throws Exception {

        mockMvc.perform(get("/api/products/" + product1.getId())) // Usa el ID de la BD
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Product 1")))
                .andExpect(jsonPath("$.description", is("Description 1")))
                .andExpect(jsonPath("$.price", is(10.0)));

        mockMvc.perform(get("/api/products/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateProduct() throws Exception {
        Product newProduct = new Product();
        newProduct.setName("New Product");
        newProduct.setDescription("New Description");
        newProduct.setPrice(new BigDecimal("7.75"));
        newProduct.setBrand(testBrand);
        newProduct.setCategory(testCategory);
        String productJson =  objectMapper.writeValueAsString(newProduct);
        String response = mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("New Product")))
                .andExpect(jsonPath("$.description", is("New Description")))
                .andExpect(jsonPath("$.price", is(7.75)))
                .andReturn().getResponse().getContentAsString();
        System.out.println("Response: " + response);

        ProductDTO actualProduct = objectMapper.readValue(response, ProductDTO.class);
        assertEquals("New Product", actualProduct.getName());
    assertEquals("New Description", actualProduct.getDescription());
    assertEquals(new BigDecimal("7.75"), actualProduct.getPrice()); // Mejor comparación de BigDecimal

}

    @Test
    void testUpdateProduct() throws Exception {
        Product updatedProduct = new Product();
        updatedProduct.setId(product1.getId()); 
        updatedProduct.setName("Updated Product");
        updatedProduct.setDescription("Updated Description");
        updatedProduct.setPrice(new BigDecimal("33.30"));
        updatedProduct.setBrand(testBrand);
        updatedProduct.setCategory(testCategory);

        String productJson = objectMapper.writeValueAsString(updatedProduct);

        mockMvc.perform(put("/api/products/" + product1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Updated Product")))
                .andExpect(jsonPath("$.description", is("Updated Description")))
                .andExpect(jsonPath("$.price", is(33.30)));

        mockMvc.perform(put("/api/products/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productJson))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteProduct() throws Exception {

        mockMvc.perform(delete("/api/products/" + product1.getId())) // Usa el ID de la BD
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/products/" + product1.getId())) // Verifica que ya no existe
                .andExpect(status().isNotFound());

        mockMvc.perform(delete("/api/products/999"))
                .andExpect(status().isNotFound()); 
    }

    @Test
    void testSearchProducts() throws Exception {
        Product product2 = new Product();
        product2.setName("Product Beta");
        product2.setDescription("Description 2");
        product2.setPrice(new BigDecimal("20.00"));
        product2.setBrand(testBrand);
        product2.setCategory(testCategory);
        productRepository.save(product2);

        Product product3 = new Product();
        product3.setName("Alpha Product");
        product3.setDescription("Description 3");
        product3.setPrice(new BigDecimal("15.00"));
        product3.setBrand(testBrand);
        product3.setCategory(testCategory);
        productRepository.save(product3);

        mockMvc.perform(get("/api/products/search").param("name", "Product"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].name", is("Product 1")))
                .andExpect(jsonPath("$[1].name", is("Product Beta")))
                .andExpect(jsonPath("$[2].name", is("Alpha Product")));

        mockMvc.perform(get("/api/products/search").param("name", "product"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));

        mockMvc.perform(get("/api/products/search").param("category", "Test Category"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));

        mockMvc.perform(get("/api/products/search").param("minPrice", "12"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].price", is(20.00)))
                .andExpect(jsonPath("$[1].price", is(15.00)));

        mockMvc.perform(get("/api/products/search").param("maxPrice", "18"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].price", is(10.00)))
                .andExpect(jsonPath("$[1].price", is(15.00)));

        mockMvc.perform(get("/api/products/search")
                        .param("minPrice", "12")
                        .param("maxPrice", "25"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].price", is(20.00)))
                .andExpect(jsonPath("$[1].price", is(15.00)));

        mockMvc.perform(get("/api/products/search")
                        .param("name", "Product")
                        .param("category", "Test Category"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));

        mockMvc.perform(get("/api/products/search")
                        .param("name", "Product")
                        .param("category", "Test Category")
                        .param("minPrice", "10")
                        .param("maxPrice", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));
    }

}
