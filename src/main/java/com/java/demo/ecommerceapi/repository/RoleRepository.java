package com.java.demo.ecommerceapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.java.demo.ecommerceapi.model.Role;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
}
