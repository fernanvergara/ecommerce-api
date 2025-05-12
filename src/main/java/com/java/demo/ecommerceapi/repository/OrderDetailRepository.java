package com.java.demo.ecommerceapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.java.demo.ecommerceapi.model.OrderDetail;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
    // Agregar los que se consideren necesarios
}