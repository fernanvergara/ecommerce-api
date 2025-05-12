package com.java.demo.ecommerceapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.java.demo.ecommerceapi.model.Order;
import com.java.demo.ecommerceapi.model.OrderDetail;
import com.java.demo.ecommerceapi.model.Product;
import com.java.demo.ecommerceapi.service.OrderService;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/create")
    public ResponseEntity<Order> createOrder(@RequestBody List<Map<String, Object>> orderDetailsData) {
        // Obtener el nombre de usuario del usuario autenticado
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        // Convertir la lista de mapas a una lista de OrderDetail
        List<OrderDetail> orderDetails = orderDetailsData.stream()
                .map(itemData -> {
                    OrderDetail orderDetail = new OrderDetail();
                    Product product = new Product();
                    product.setId(((Number) itemData.get("productId")).longValue());  // Usar Number y luego convertir
                    orderDetail.setProduct(product);
                    orderDetail.setQuantity(((Number) itemData.get("quantity")).intValue()); // Usar Number y luego convertir
                    return orderDetail;
                })
                .collect(Collectors.toList());

        Order order = orderService.createOrder(username, orderDetails);
        return new ResponseEntity<>(order, HttpStatus.CREATED);
    }

    @GetMapping("/user")
    public ResponseEntity<List<Order>> getOrdersByUser() {
        // Obtener el nombre de usuario del usuario autenticado
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        List<Order> orders = orderService.getOrdersByUser(username);
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<Order>> getOrderById(@PathVariable Long id) {
        Optional<Order> order = orderService.getOrderById(id);
        if (order.isPresent()) {
            return new ResponseEntity<>(order, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
