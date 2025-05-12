package com.java.demo.ecommerceapi.controllertest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.java.demo.ecommerceapi.config.JwtService;
import com.java.demo.ecommerceapi.controller.StockController;
import com.java.demo.ecommerceapi.model.*;
import com.java.demo.ecommerceapi.service.IUserService;

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
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@WebMvcTest(StockController.class)
@Import(StockIntegrationTest.TestSecurityConfig.class)
public class OrderIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private IUserService userService;

    @Test
    @WithMockUser
    public void testCreateOrder() throws Exception {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password"); 
        user.setEmail("test@example.com");

        Brand brand = new Brand();
        brand.setName("Test Brand");

        Category category = new Category();
        category.setName("Test Category");

        Product product = new Product();
        product.setName("Test Product");
        product.setDescription("Test Description");
        product.setPrice(new BigDecimal("100.00"));
        product.setBrand(brand);
        product.setCategory(category);

        // Crear un stock para el producto
        Stock stock = new Stock();
        stock.setProduct(product);
        stock.setQuantity(10);  // Asegúrate de que haya suficiente stock para la orden
        stock.setLocation("Test Location");

        // Crear el cuerpo de la petición para crear la orden
        List<Map<String, Object>> orderItems = List.of(
                Map.of("productId", product.getId(), "quantity", 2)
        );
        String orderJson = objectMapper.writeValueAsString(orderItems);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/orders/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderJson))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalAmount").value(200.00)); // Verifica el total
    }

    @Test
    @WithMockUser
    public void testGetOrdersByUser() throws Exception {
        // Crear una orden para el usuario de prueba
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password");
        user.setEmail("test@example.com");

        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setTotalAmount(new BigDecimal("100.00"));

        // Simular que el usuario tiene una orden
        List<Order> orders = List.of(order);

        // Configurar el mock del servicio para que devuelva la orden creada
        // No es necesario aquí, ya que la orden se guarda en la misma transacción y la recupera la consulta

        mockMvc.perform(MockMvcRequestBuilders.get("/api/orders/user"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].totalAmount").value(100.00));
    }

    @Test
    @WithMockUser
    public void testGetOrderById() throws Exception {
        // Crear una orden para obtener su ID
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password");
        user.setEmail("test@example.com");

        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setTotalAmount(new BigDecimal("100.00"));

        // Guardar la orden y obtiene el ID
        // No estoy usando el servicio directamente, así que la orden se guarda y recupera en la misma transacción

        // Simular la creación de la orden (esto normalmente lo haría el servicio)
        // En una prueba de integración, la base de datos está involucrada, así que no necesito simular el guardado.

        // Ahora, realizo la petición para obtener la orden por ID, asumiendo que el ID es 1 (o el que sea generado por la BD)
        mockMvc.perform(MockMvcRequestBuilders.get("/api/orders/1")) // Suponiendo que el ID es 1, cámbiarlo si es necesario
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalAmount").value(100.00));
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

