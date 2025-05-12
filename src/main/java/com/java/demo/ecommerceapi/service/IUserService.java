package com.java.demo.ecommerceapi.service;

import com.java.demo.ecommerceapi.model.User;

import java.util.Optional;

public interface IUserService {
    Optional<User> findByUsername(String username);
    
}
