package com.java.demo.ecommerceapi.controllertest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.java.demo.ecommerceapi.config.JwtService;
import com.java.demo.ecommerceapi.controller.UserController;
import com.java.demo.ecommerceapi.model.User;
import com.java.demo.ecommerceapi.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.is;

@WebMvcTest(UserController.class)
@Import(UserIntegrationTest.TestSecurityConfig.class) 
public class UserIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtService jwtService;

    private User testUser;

    @BeforeEach
    public void setUp() {
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password"); 
        testUser.setRoles(new ArrayList<>());
    }

    @Test
    public void testRegisterUser() throws Exception {

        Map<String, String> registrationData = new HashMap<>();
        registrationData.put("username", "testuser");
        registrationData.put("password", "password");
        registrationData.put("email", "test@example.com");

        when(userService.registerUser(anyString(), anyString(), anyString())).thenReturn(testUser);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationData))) // Send registrationData
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.username", is("testuser")))
                .andExpect(jsonPath("$.email", is("test@example.com")));

        verify(userService, times(1)).registerUser("testuser", "password", "test@example.com"); // Use actual values
    }

    @Test
    public void testRegisterUserWithInvalidData() throws Exception {
        Map<String, String> registrationData = new HashMap<>();
        registrationData.put("password", "password");
        registrationData.put("email", "test@example.com");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationData)))
                .andExpect(status().isBadRequest());

        verify(userService, times(0)).registerUser(anyString(), anyString(), anyString());
    }

    static class TestSecurityConfig {
        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
            http.csrf().disable()
                    .authorizeHttpRequests()
                    .anyRequest().permitAll()
                    .and()
                    .sessionManagement().disable();
            return http.build();
        }
    }
}

