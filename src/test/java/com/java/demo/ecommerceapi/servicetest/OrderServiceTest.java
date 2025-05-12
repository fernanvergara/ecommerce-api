package com.java.demo.ecommerceapi.servicetest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.java.demo.ecommerceapi.model.Order;
import com.java.demo.ecommerceapi.model.OrderDetail;
import com.java.demo.ecommerceapi.model.Product;
import com.java.demo.ecommerceapi.model.Stock;
import com.java.demo.ecommerceapi.model.User;
import com.java.demo.ecommerceapi.repository.OrderDetailRepository;
import com.java.demo.ecommerceapi.repository.OrderRepository;
import com.java.demo.ecommerceapi.service.OrderService;
import com.java.demo.ecommerceapi.service.ProductService;
import com.java.demo.ecommerceapi.service.StockService;
import com.java.demo.ecommerceapi.service.UserService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderDetailRepository orderItemRepository;

    @Mock
    private UserService userService;

    @Mock
    private ProductService productService;

    @Mock
    private StockService stockService;

    @InjectMocks
    private OrderService orderService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateOrder() {
        // Arrange
        String username = "testuser";
        User user = new User(1L, username, "password", "test@example.com", new ArrayList<>(), null);

        Product product1 = new Product(1L, "Product 1", "Description 1", new BigDecimal("10.00"), null, null, null, null);
        Product product2 = new Product(2L, "Product 2", "Description 2", new BigDecimal("20.00"), null, null, null, null);

        OrderDetail orderDetail1 = new OrderDetail();
        orderDetail1.setProduct(product1);
        orderDetail1.setQuantity(2);

        OrderDetail orderDetail2 = new OrderDetail();
        orderDetail2.setProduct(product2);
        orderDetail2.setQuantity(1);

        List<OrderDetail> orderDetails = List.of(orderDetail1, orderDetail2);

        Order savedOrder = new Order();
        savedOrder.setId(100L);
        savedOrder.setUser(user);
        savedOrder.setOrderDate(LocalDateTime.now());
        savedOrder.setTotalAmount(new BigDecimal("40.00"));
        savedOrder.setOrderDetails(orderDetails);

        when(userService.findByUsername(username)).thenReturn(Optional.of(user));
        when(productService.getProductById(1L)).thenReturn(Optional.of(product1));
        when(productService.getProductById(2L)).thenReturn(Optional.of(product2));
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
        when(stockService.getStocksByProductId(1L)).thenReturn(List.of(new Stock(1L, product1, 10, "Location1")));
        when(stockService.getStocksByProductId(2L)).thenReturn(List.of(new Stock(2L, product2, 10, "Location1")));

        // Act
        Order result = orderService.createOrder(username, orderDetails);

        // Assert
        assertEquals(100L, result.getId());
        assertEquals(username, result.getUser().getUsername());
        assertEquals(new BigDecimal("40.00"), result.getTotalAmount());
        assertEquals(2, result.getOrderDetails().size());
        verify(userService, times(1)).findByUsername(username);
        verify(productService, times(1)).getProductById(1L);
        verify(productService, times(1)).getProductById(2L);
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(orderItemRepository, times(1)).saveAll(orderDetails);
        verify(stockService, times(2)).getStocksByProductId(anyLong());
        verify(stockService, times(2)).updateStockQuantity(anyLong(), anyString(), anyInt());
    }

    @Test
    public void testGetOrdersByUser() {
        // Arrange
        String username = "testuser";
        User user = new User(1L, username, "password", "test@example.com", new ArrayList<>(), null);
        List<Order> orders = new ArrayList<>();
        orders.add(new Order());
        orders.add(new Order());

        when(userService.findByUsername(username)).thenReturn(Optional.of(user));
        when(orderRepository.findByUser(user)).thenReturn(orders);

        // Act
        List<Order> result = orderService.getOrdersByUser(username);

        // Assert
        assertEquals(2, result.size());
        verify(userService, times(1)).findByUsername(username);
        verify(orderRepository, times(1)).findByUser(user);
    }

    @Test
    public void testGetOrderById() {
        // Arrange
        Order order = new Order();
        order.setId(100L);
        when(orderRepository.findById(100L)).thenReturn(Optional.of(order));

        // Act
        Optional<Order> result = orderService.getOrderById(100L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(100L, result.get().getId());
        verify(orderRepository, times(1)).findById(100L);
    }
}
