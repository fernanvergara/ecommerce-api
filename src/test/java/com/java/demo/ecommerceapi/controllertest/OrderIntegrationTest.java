package com.java.demo.ecommerceapi.controllertest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.java.demo.ecommerceapi.enums.OrderStatus;
import com.java.demo.ecommerceapi.model.*;
import com.java.demo.ecommerceapi.repository.*;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest 
@AutoConfigureMockMvc
@Transactional
public class OrderIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private StockRepository stockRepository;

    private User user;
    private Brand brand;
    private Category category;
    private Product product;
    private Stock stock;

    @BeforeEach
    public void setUp() {
        // Limpiar la base de datos antes de cada prueba
        orderRepository.deleteAll();
        stockRepository.deleteAll();
        productRepository.deleteAll();
        categoryRepository.deleteAll();
        brandRepository.deleteAll();
        userRepository.deleteAll();

        // Crear y guardar un usuario
        user = new User();
        user.setUsername("testuser");
        user.setPassword("password");
        user.setEmail("test@example.com");
        user = userRepository.save(user);

        // Crear y guardar una marca
        brand = new Brand();
        brand.setName("Test Brand");
        brand = brandRepository.save(brand);

        // Crear y guardar una categoría
        category = new Category();
        category.setName("Test Category");
        category = categoryRepository.save(category);

        // Crear y guardar un producto
        product = new Product();
        product.setName("Test Product");
        product.setDescription("Test Description");
        product.setPrice(new BigDecimal("100"));
        product.setBrand(brand);
        product.setCategory(category);
        product = productRepository.save(product);

        // Crear y guardar stock para el producto
        stock = new Stock();
        stock.setProduct(product);
        stock.setQuantity(10);
        stock.setLocation("Test Location");
        stock = stockRepository.save(stock);
    }

    @Test
    @WithMockUser(username = "testuser")
    public void testCreateOrder() throws Exception {
        // Crear el cuerpo de la petición para crear la orden
        List<Map<String, Object>> orderItems = List.of(
                Map.of("productId", product.getId(), "quantity", 2)
        );
        String orderJson = objectMapper.writeValueAsString(orderItems);

        mockMvc.perform(post("/api/orders/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderJson))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.totalAmount").value(200));
    }

    @Test
    @WithMockUser(username = "testuser")
    public void testGetOrdersByUser() throws Exception {
        // Crear una orden para el usuario de prueba
        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setTotalAmount(new BigDecimal("100"));
        order.setStatus(OrderStatus.PENDING);
        orderRepository.save(order); // Guardar la orden en la base de datos

        mockMvc.perform(get("/api/orders/user"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].totalAmount").value(100));
    }

    @Test
    @WithMockUser(username = "testuser")
    public void testGetOrderById() throws Exception {
        // Crear una orden para obtener su ID
        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setTotalAmount(new BigDecimal("100"));
        order.setStatus(OrderStatus.PENDING);
        order = orderRepository.save(order); // Guardar la orden y obtenerla con el ID asignado

        mockMvc.perform(get("/api/orders/" + order.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.totalAmount").value(100.00));
    }
}
