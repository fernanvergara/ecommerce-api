package com.java.demo.ecommerceapi.repository;

import com.java.demo.ecommerceapi.model.Stock;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {
    Optional<Stock> findByProductIdAndLocation(Long productId, String location);
    List<Stock> findByProductId(Long productId);
}
