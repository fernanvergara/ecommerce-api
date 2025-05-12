package com.java.demo.ecommerceapi.repository;

import com.java.demo.ecommerceapi.model.Order;
import com.java.demo.ecommerceapi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser(User usuario);
}
