package com.java.demo.ecommerceapi.controllertest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.java.demo.ecommerceapi.model.Brand;
import com.java.demo.ecommerceapi.model.Category;
import com.java.demo.ecommerceapi.model.Product;
import com.java.demo.ecommerceapi.model.Stock;
import com.java.demo.ecommerceapi.repository.BrandRepository;
import com.java.demo.ecommerceapi.repository.CategoryRepository;
import com.java.demo.ecommerceapi.repository.ProductRepository;
import com.java.demo.ecommerceapi.repository.StockRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class StockIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private Product testProduct; // Producto de prueba compartido entre los tests
    private Stock stock1, stock2;
    private Brand brand;
    private Category category;


    @BeforeEach
    void setUp() {
        brandRepository.deleteAll();
        categoryRepository.deleteAll();
        productRepository.deleteAll();
        stockRepository.deleteAll();

        brand = new Brand();
        brand.setName("Test Brand"); 
        brand = brandRepository.save( brand );
        category = new Category();
        category.setName("Test Category");
        category = categoryRepository.save(category);

        testProduct = new Product();
        testProduct.setName("Test Product");
        testProduct.setDescription("Test Description");
        testProduct.setPrice(new BigDecimal(10.0));
        testProduct.setBrand(brand);
        testProduct.setCategory(category);
        testProduct = productRepository.save(testProduct);

        stock1 = new Stock();
        stock1.setProduct(testProduct);
        stock1.setQuantity(10);
        stock1.setLocation("Warehouse A");
        stock1 = stockRepository.save(stock1);

        stock2 = new Stock();
        stock2.setProduct(testProduct);
        stock2.setQuantity(20);
        stock2.setLocation("Warehouse B");
        stock2 = stockRepository.save(stock2);
    }

    @Test
    @WithMockUser(username = "testUser")
    void testGetAllStocks() throws Exception {
        mockMvc.perform(get("/api/stocks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].quantity", is(10)))
                .andExpect(jsonPath("$[0].location", is("Warehouse A")))
                .andExpect(jsonPath("$[1].quantity", is(20)))
                .andExpect(jsonPath("$[1].location", is("Warehouse B")));
    }

    @Test
    @WithMockUser(username = "testUser")
    void testGetStockById() throws Exception {
        mockMvc.perform(get("/api/stocks/" + stock1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity", is(10)))
                .andExpect(jsonPath("$.location", is("Warehouse A")));

        mockMvc.perform(get("/api/stocks/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "testUser")
    void testCreateStock() throws Exception {
        Stock newStock = new Stock(null, testProduct, 5, "Warehouse D");

        mockMvc.perform(post("/api/stocks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newStock)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.quantity", is(5)))
                .andExpect(jsonPath("$.location", is("Warehouse D")));
    }

    @Test
    @WithMockUser(username = "testUser")
    void testUpdateStock() throws Exception {
        Stock updatedStock = new Stock(1L, testProduct, 30, "Warehouse F");

        mockMvc.perform(put("/api/stocks/" + stock1.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedStock)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity", is(30)))
                .andExpect(jsonPath("$.location", is("Warehouse F")));

        mockMvc.perform(put("/api/stocks/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedStock)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "testUser")
    void testDeleteStock() throws Exception {
        mockMvc.perform(delete("/api/stocks/" + stock1.getId()))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/api/stocks/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "testUser")
    void testGetStockByProductIdAndLocation() throws Exception {
        mockMvc.perform(get("/api/stocks/product/" + testProduct.getId() + "/location/"+stock1.getLocation()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity", is(10)))
                .andExpect(jsonPath("$.location", is("Warehouse A")));

        mockMvc.perform(get("/api/stocks/product/" + testProduct.getId() + "/location/NonExistent"))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/api/stocks/product/99999" + "/location/Location X"))
                .andExpect(status().isNotFound());
    }

}

