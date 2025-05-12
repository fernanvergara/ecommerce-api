package com.java.demo.ecommerceapi.controllertest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.java.demo.ecommerceapi.model.Product;
import com.java.demo.ecommerceapi.model.Stock;
import com.java.demo.ecommerceapi.config.JwtService;
import com.java.demo.ecommerceapi.controller.ProductController;
import com.java.demo.ecommerceapi.model.Brand;
import com.java.demo.ecommerceapi.model.Category;
import com.java.demo.ecommerceapi.repository.ProductRepository;
import com.java.demo.ecommerceapi.service.BrandService;
import com.java.demo.ecommerceapi.service.CategoryService;
import com.java.demo.ecommerceapi.service.IUserService;
import com.java.demo.ecommerceapi.service.ProductService;
import com.java.demo.ecommerceapi.repository.BrandRepository;
import com.java.demo.ecommerceapi.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional; // Importa la anotación @Transactional
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
@Import(ProductIntegrationTest.TestSecurityConfig.class)
public class ProductIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductService productService;

    @MockBean
    private BrandService brandService;

    @MockBean
    private CategoryService categoryService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private IUserService userService;

    private Brand testBrand;
    private Category testCategory;

    @BeforeEach
    void setUp() {
        testBrand = new Brand();
        testBrand.setName("Test Brand");
 
        testCategory = new Category();
        testCategory.setName("Test Category");
    }

    @Test
    void testGetAllProducts() throws Exception {
        Product product1 = new Product();
        product1.setName("Product 1");
        product1.setDescription("Description 1");
        product1.setPrice(new BigDecimal("10.00"));
        product1.setBrand(testBrand);
        product1.setCategory(testCategory);
 
        Product product2 = new Product();
        product2.setName("Product 2");
        product2.setDescription("Description 2");
        product2.setPrice(new BigDecimal("20.00"));
        product2.setBrand(testBrand);
        product2.setCategory(testCategory);
 
        List<Product> products = List.of(product1, product2);
        when(productService.getAllProducts()).thenReturn(products);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("Product 1")))
                .andExpect(jsonPath("$[0].description", is("Description 1")))
                .andExpect(jsonPath("$[0].price", is(10.00)))
                .andExpect(jsonPath("$[1].name", is("Product 2")))
                .andExpect(jsonPath("$[1].description", is("Description 2")))
                .andExpect(jsonPath("$[1].price", is(20.00)));
    }

    @Test
    void testGetProductById() throws Exception {
        Product product1 = new Product();
        product1.setId(1L);
        product1.setName("Product A");
        product1.setDescription("Description A");
        product1.setPrice(new BigDecimal("15.50"));
        product1.setBrand(testBrand);
        product1.setCategory(testCategory);
        when(productService.getProductById(1L)).thenReturn(Optional.of(product1) );
        when(productService.getProductById(999L)).thenReturn(Optional.empty() );

        mockMvc.perform(MockMvcRequestBuilders.get("/api/products/" + product1.getId())) // Usa el ID de la BD
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Product A")))
                .andExpect(jsonPath("$.description", is("Description A")))
                .andExpect(jsonPath("$.price", is(15.50)));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/products/999"))
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
        when(productService.createProduct(any(Product.class))).thenReturn(newProduct);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newProduct)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("New Product")))
                .andExpect(jsonPath("$.description", is("New Description")))
                .andExpect(jsonPath("$.price", is(7.75)));
    }

    @Test
    void testUpdateProduct() throws Exception {
        Product product1 = new Product();
        product1.setId(1L);
        product1.setName("Product B");
        product1.setDescription("Description B");
        product1.setPrice(new BigDecimal("22.25"));
        product1.setBrand(testBrand);
        product1.setCategory(testCategory);

        Product updatedProduct = new Product();
        updatedProduct.setId(product1.getId()); 
        updatedProduct.setName("Updated Product");
        updatedProduct.setDescription("Updated Description");
        updatedProduct.setPrice(new BigDecimal("33.30"));
        updatedProduct.setBrand(testBrand);
        updatedProduct.setCategory(testCategory);

        when(productService.updateProduct(eq(1L), any(Product.class))).thenReturn(updatedProduct);
        when(productService.updateProduct(eq(999L), any(Product.class))).thenReturn(null); 
        when(productService.getProductById(1L)).thenReturn(Optional.of(product1));
        when(productService.getProductById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.put("/api/products/" + product1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedProduct)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Updated Product")))
                .andExpect(jsonPath("$.description", is("Updated Description")))
                .andExpect(jsonPath("$.price", is(33.30)));

        mockMvc.perform(MockMvcRequestBuilders.put("/api/products/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedProduct)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteProduct() throws Exception {
        Product product1 = new Product();
        product1.setId(1L);
        product1.setName("Product C");
        product1.setDescription("Description C");
        product1.setPrice(new BigDecimal("9.99"));
        product1.setBrand(testBrand);
        product1.setCategory(testCategory);
;
        when(productService.getProductById(1L)).thenReturn(Optional.of(product1) );
        doNothing().when(productService).deleteProduct(1L);
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/products/" + product1.getId())) // Usa el ID de la BD
                .andExpect(status().isOk());


        when(productService.getProductById(1L)).thenReturn(Optional.empty());
        doNothing().when(productService).deleteProduct(1L);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/products/" + product1.getId())) // Verifica que ya no existe
                .andExpect(status().isNotFound());

        doNothing().when(productService).deleteProduct(999L);
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/products/999"))
                .andExpect(status().isOk()); // El controlador debe manejar la eliminación de un no existente
    }

    @Test
    void testSearchProducts() throws Exception {
        Product product1 = new Product();
        product1.setName("Product Alpha");
        product1.setDescription("Description 1");
        product1.setPrice(new BigDecimal("10.00"));
        product1.setBrand(testBrand);
        product1.setCategory(testCategory);

        Product product2 = new Product();
        product2.setName("Product Beta");
        product2.setDescription("Description 2");
        product2.setPrice(new BigDecimal("20.00"));
        product2.setBrand(testBrand);
        product2.setCategory(testCategory);

        Product product3 = new Product();
        product3.setName("Alpha Product");
        product3.setDescription("Description 3");
        product3.setPrice(new BigDecimal("15.00"));
        product3.setBrand(testBrand);
        product3.setCategory(testCategory);

        List<Product> products = List.of(product1, product2, product3);

        when(productService.searchProductsByName("Alpha")).thenReturn( List.of(product1, product3) );
        mockMvc.perform(MockMvcRequestBuilders.get("/api/products/search").param("name", "Alpha"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("Alpha Product")))
                .andExpect(jsonPath("$[1].name", is("Product Alpha")));

        when(productService.searchProductsByName("alpha")).thenReturn( List.of(product1, product3) );
        mockMvc.perform(MockMvcRequestBuilders.get("/api/products/search").param("name", "alpha"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));

        when(productService.searchProductsByCategory("Test Category")).thenReturn( List.of(product1, product2, product3) );
        mockMvc.perform(MockMvcRequestBuilders.get("/api/products/search").param("category", "Test Category"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));

        when(productService.searchProductsByPriceRange(new BigDecimal(12), null)).thenReturn( List.of( product2, product3) );
        mockMvc.perform(MockMvcRequestBuilders.get("/api/products/search").param("minPrice", "12.00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].price", is(15.00)))
                .andExpect(jsonPath("$[1].price", is(20.00)));

        when(productService.searchProductsByPriceRange(null, new BigDecimal(18))).thenReturn( List.of( product1, product2) );
        mockMvc.perform(MockMvcRequestBuilders.get("/api/products/search").param("maxPrice", "18.00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].price", is(10.00)))
                .andExpect(jsonPath("$[1].price", is(15.00)));

        when(productService.searchProductsByPriceRange(new BigDecimal(12), new BigDecimal(25))).thenReturn( List.of( product2, product3) );
        mockMvc.perform(MockMvcRequestBuilders.get("/api/products/search")
                        .param("minPrice", "12.00")
                        .param("maxPrice", "25.00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].price", is(15.00)))
                .andExpect(jsonPath("$[1].price", is(20.00)));

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
