package com.java.demo.ecommerceapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.java.demo.ecommerceapi.exception.GeneralException;
import com.java.demo.ecommerceapi.exception.ObjectNotFoundException;
import com.java.demo.ecommerceapi.exception.UserAlreadyExistsException;
import com.java.demo.ecommerceapi.model.User;
import com.java.demo.ecommerceapi.service.UserService;

import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.Map;

@Tag(name = "Users", description = "Endpoints for user registration, created by default with the USER role")
@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody Map<String, String> data) {
        try {
            if (!data.containsKey("username") || data.get("username") == null || data.get("username").isEmpty() ||
                !data.containsKey("password") || data.get("password") == null || data.get("password").isEmpty() ||
                !data.containsKey("email") || data.get("email") == null || data.get("email").isEmpty()) {
                throw new GeneralException("A very important piece of information is missing.");
            }

            String username = data.get("username");
            String password = data.get("password");
            String email = data.get("email");

            User registeredUser = userService.registerUser(username, password, email);
            return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);
        } catch (GeneralException | UserAlreadyExistsException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (ObjectNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
