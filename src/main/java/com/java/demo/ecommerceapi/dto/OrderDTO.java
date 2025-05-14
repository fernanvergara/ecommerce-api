package com.java.demo.ecommerceapi.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.java.demo.ecommerceapi.enums.OrderStatus;
import com.java.demo.ecommerceapi.model.Order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {

    private Long id;
    private Long userId;
    private LocalDateTime orderDate;
    private BigDecimal totalAmount;
    private OrderStatus status;

    public OrderDTO(Order order){
        this.id = order.getId();
        this.userId = order.getUser().getId();
        this.orderDate = order.getOrderDate();
        this.totalAmount = order.getTotalAmount();
        this.status = order.getStatus();
    }
}

