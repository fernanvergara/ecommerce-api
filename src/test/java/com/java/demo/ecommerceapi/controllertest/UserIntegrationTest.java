package com.java.demo.ecommerceapi.controllertest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.java.demo.ecommerceapi.config.JwtService;
import com.java.demo.ecommerceapi.controller.UserController;
import com.java.demo.ecommerceapi.model.Role;
import com.java.demo.ecommerceapi.model.User;
import com.java.demo.ecommerceapi.repository.RoleRepository;
import com.java.demo.ecommerceapi.repository.UserRepository;
import com.java.demo.ecommerceapi.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.is;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class UserIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private User testUser;
    private Role role;

    @BeforeEach
    public void setUp() {
        userRepository.deleteAll();
        roleRepository.deleteAll();

        role = new Role();
        role.setName("ROLE_USER");
        role = roleRepository.save(role);

        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password"); 
        testUser.setRoles(List.of(role));
        testUser = userRepository.save(testUser);
    }

    @Test
    public void testRegisterUser_New() throws Exception {

        Map<String, String> registrationData = new HashMap<>();
        registrationData.put("username", "newuser");
        registrationData.put("password", "password");
        registrationData.put("email", "newtest@example.com");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationData))) // Send registrationData
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.username", is("newuser")))
                .andExpect(jsonPath("$.email", is("newtest@example.com")));
    }

    @Test
    public void testRegisterUser_ExistingUserName() throws Exception {

        Map<String, String> registrationData = new HashMap<>();
        registrationData.put("username", "testuser");
        registrationData.put("password", "password");
        registrationData.put("email", "newtest@example.com");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationData))) // Send registrationData
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testRegisterUser_ExistingEmail() throws Exception {

        Map<String, String> registrationData = new HashMap<>();
        registrationData.put("username", "newuser");
        registrationData.put("password", "password");
        registrationData.put("email", "test@example.com");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationData))) // Send registrationData
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testRegisterUser_Invalid() throws Exception {

        Map<String, String> registrationData = new HashMap<>();
        registrationData.put("username", "");
        registrationData.put("password", "password");
        registrationData.put("email", "test@example.com");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationData))) // Send registrationData
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testRegisterUserWithOutData() throws Exception {
        Map<String, String> registrationData = new HashMap<>();
        registrationData.put("password", "password");
        registrationData.put("email", "test@example.com");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationData)))
                .andExpect(status().isBadRequest());

    }


}

