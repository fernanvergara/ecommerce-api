package com.java.demo.ecommerceapi.controllertest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.java.demo.ecommerceapi.config.JwtService;
import com.java.demo.ecommerceapi.controller.StockController;
import com.java.demo.ecommerceapi.model.Brand;
import com.java.demo.ecommerceapi.model.Category;
import com.java.demo.ecommerceapi.model.Product;
import com.java.demo.ecommerceapi.model.Stock;
import com.java.demo.ecommerceapi.service.BrandService;
import com.java.demo.ecommerceapi.service.CategoryService;
import com.java.demo.ecommerceapi.service.IUserService;
import com.java.demo.ecommerceapi.service.ProductService;
import com.java.demo.ecommerceapi.service.StockService;

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
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StockController.class)
@Import(StockIntegrationTest.TestSecurityConfig.class)
public class StockIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private StockService stockService;

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

    private Product testProduct; // Producto de prueba compartido entre los tests
    private Stock stock1, stock2;

    @BeforeEach
    void setUp() {
        Brand brand = new Brand(1L, "Test Brand", null);
        Category category = new Category(1L, "Test Category", null);

        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Test Product");
        testProduct.setDescription("Test Description");
        testProduct.setPrice(new BigDecimal(10.0));
        testProduct.setBrand(brand);
        testProduct.setCategory(category);

        stock1 = new Stock();
        stock1.setId(1L);
        stock1.setProduct(testProduct);
        stock1.setQuantity(10);
        stock1.setLocation("Warehouse A");

        stock2 = new Stock();
        stock2.setId(2L);
        stock2.setProduct(testProduct);
        stock2.setQuantity(20);
        stock2.setLocation("Warehouse B");
    }

    @Test
    @WithMockUser
    void testGetAllStocks() throws Exception {
        List<Stock> stocks = Arrays.asList(stock1, stock2);
        when(stockService.getAllStocks()).thenReturn(stocks);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/stocks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].quantity", is(10)))
                .andExpect(jsonPath("$[0].location", is("Warehouse A")))
                .andExpect(jsonPath("$[1].quantity", is(20)))
                .andExpect(jsonPath("$[1].location", is("Warehouse B")));
    }

    @Test
    void testGetStockById() throws Exception {
        when(stockService.getStockById(1L)).thenReturn(Optional.of(stock1));
        when(stockService.getStockById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/stocks/" + stock1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity", is(10)))
                .andExpect(jsonPath("$.location", is("Warehouse A")));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/stocks/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateStock() throws Exception {
        Stock newStock = new Stock();
        newStock.setProduct(testProduct);
        newStock.setQuantity(5);
        newStock.setLocation("Warehouse D");

        Stock savedStock = new Stock(3L, testProduct, 5, "Warehouse D");
        when(stockService.createStock(any(Stock.class))).thenReturn(savedStock);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/stocks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newStock)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.quantity", is(5)))
                .andExpect(jsonPath("$.location", is("Warehouse D")));
    }

    @Test
    void testUpdateStock() throws Exception {
        Stock updatedStock = new Stock(1L, testProduct, 30, "Warehouse F");
        when(stockService.updateStock(eq(1L), any(Stock.class))).thenReturn(updatedStock);
        when(stockService.updateStock(eq(999L), any(Stock.class))).thenReturn(null); 
        when(stockService.getStockById(1L)).thenReturn(Optional.of(stock1));
        when(stockService.getStockById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.put("/api/stocks/" + stock1.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedStock)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity", is(30)))
                .andExpect(jsonPath("$.location", is("Warehouse F")));

        mockMvc.perform(MockMvcRequestBuilders.put("/api/stocks/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedStock)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteStock() throws Exception {
        when(stockService.getStockById(1L)).thenReturn(Optional.of(stock1));
        doNothing().when(stockService).deleteStock(1L);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/stocks/" + stock1.getId()))
                .andExpect(status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/stocks/999"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetStockByProductIdAndLocation() throws Exception {
        when(stockService.getStockByProductIdAndLocation(testProduct.getId(), "Location X")).thenReturn(Optional.of(stock1));
        when(stockService.getStockByProductIdAndLocation(testProduct.getId(), "NonExistent")).thenReturn(Optional.empty());
        when(stockService.getStockByProductIdAndLocation(99999L, "Location X")).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/stocks/product/" + testProduct.getId() + "/location/Location X"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity", is(10)))
                .andExpect(jsonPath("$.location", is("Warehouse A")));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/stocks/product/" + testProduct.getId() + "/location/NonExistent"))
                .andExpect(status().isNotFound());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/stocks/product/99999" + "/location/Location X"))
                .andExpect(status().isNotFound());
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

