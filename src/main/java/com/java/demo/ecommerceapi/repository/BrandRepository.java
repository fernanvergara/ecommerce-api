package com.java.demo.ecommerceapi.repository;

import com.java.demo.ecommerceapi.model.Brand;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Long> {
    Optional<Brand> findByName(String name);
}