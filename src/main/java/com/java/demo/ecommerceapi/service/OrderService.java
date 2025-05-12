/**
 * Nota:
 * Estoy omitiendo el uso de la interfaz para los servicios
 */
package com.java.demo.ecommerceapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.java.demo.ecommerceapi.enums.OrderStatus;
import com.java.demo.ecommerceapi.exception.GeneralException;
import com.java.demo.ecommerceapi.exception.ObjectNotFoundException;
import com.java.demo.ecommerceapi.model.Order;
import com.java.demo.ecommerceapi.model.OrderDetail;
import com.java.demo.ecommerceapi.model.Product;
import com.java.demo.ecommerceapi.model.Stock;
import com.java.demo.ecommerceapi.model.User;
import com.java.demo.ecommerceapi.repository.OrderDetailRepository;
import com.java.demo.ecommerceapi.repository.OrderRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final UserService userService;
    private final ProductService productService;
    private final StockService stockService;

    @Autowired
    public OrderService(OrderRepository orderRepository, OrderDetailRepository orderDetailRepository, UserService userService, ProductService productService, StockService stockService) {
        this.orderRepository = orderRepository;
        this.orderDetailRepository = orderDetailRepository;
        this.userService = userService;
        this.productService = productService;
        this.stockService = stockService;
    }

    @Transactional
    public Order createOrder(String username, List<OrderDetail> orderDetails) {
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new ObjectNotFoundException("User '"+username+"' not found"));

        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING); // Estado inicial

        BigDecimal totalAmount = BigDecimal.ZERO;
        for (OrderDetail orderDetail : orderDetails) {
            Product product = productService.getProductById(orderDetail.getProduct().getId())
                    .orElseThrow(() -> new ObjectNotFoundException("Product '"+orderDetail.getProduct().getName()+"' not found"));
            orderDetail.setProduct(product);
            orderDetail.setPrice(product.getPrice()); // Guarda el precio del producto en el momento de la compra
            orderDetail.setOrder(order); // Establece la relación con la orden
            totalAmount = totalAmount.add(product.getPrice().multiply(BigDecimal.valueOf(orderDetail.getQuantity())));

             // Verificar y actualizar el stock
            List<Stock> stocks = stockService.getStocksByProductId(product.getId());
            int totalStock = 0;
            for(Stock stock : stocks){
                totalStock += stock.getQuantity();
            }
            if (totalStock < orderDetail.getQuantity()) {
                throw new GeneralException("Insufficient stock for product '" + product.getName()+"'");
            }
            // Actualizar el stock (aquí se asume que hay una única ubicación de stock)
            for(Stock stock : stocks){
                 if(stock.getQuantity() >= orderDetail.getQuantity()){
                    stockService.updateStockQuantity(product.getId(), stock.getLocation(), stock.getQuantity() - orderDetail.getQuantity());
                    break;
                 }
                 else{
                    orderDetail.setQuantity(orderDetail.getQuantity() - stock.getQuantity());
                    stockService.updateStockQuantity(product.getId(), stock.getLocation(), 0);
                 }

            }
        }

        order.setTotalAmount(totalAmount);
        order.setOrderDetails(orderDetails); // Asigna la lista de orderItems a la orden
        order = orderRepository.save(order); // Guarda la orden para obtener el ID
        orderDetailRepository.saveAll(orderDetails); // Guarda los orderItems

        return order;
    }

    public List<Order> getOrdersByUser(String username) {
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new ObjectNotFoundException("User '"+username+"' not found"));
        return orderRepository.findByUser(user);
    }

    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }
}
