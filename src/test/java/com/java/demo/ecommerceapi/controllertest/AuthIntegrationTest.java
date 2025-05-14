package com.java.demo.ecommerceapi.controllertest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "testUser")
    public void testRegister() throws Exception {
        // Crea un nuevo usuario para registrar
        Map<String, String> user = new HashMap<>();
        user.put("username", "newuser");
        user.put("password", "password123");
        user.put("email", "newuser@example.com");
        String userJson = objectMapper.writeValueAsString(user);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("newuser"));
    }

    @Test
    @WithMockUser(username = "testUser")
    public void testLogin() throws Exception {
        // Primero, registra un usuario
        Map<String, String> user = new HashMap<>();
        user.put("username", "testuser");
        user.put("password", "password");
        user.put("email", "test@example.com");
        String userJson = objectMapper.writeValueAsString(user);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        // Luego, intenta iniciar sesión con el usuario registrado
        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("username", "testuser");
        loginRequest.put("password", "password");
        String loginJson = objectMapper.writeValueAsString(loginRequest);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.token").exists()); // Verifica que se devuelve un token
    }

    @Test
    @WithMockUser(username = "testUser")
    public void testLogin_UserNoExists() throws Exception {
        // Primero, registra un usuario
        Map<String, String> user = new HashMap<>();
        user.put("username", "testuser");
        user.put("password", "password");
        user.put("email", "test@example.com");
        String userJson = objectMapper.writeValueAsString(user);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        // Luego, intenta iniciar sesión con el usuario registrado
        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("username", "testuser1");
        loginRequest.put("password", "password");
        String loginJson = objectMapper.writeValueAsString(loginRequest);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "testUser")
    public void testLogin_BadPassword() throws Exception {
        // Primero, registra un usuario
        Map<String, String> user = new HashMap<>();
        user.put("username", "testuser");
        user.put("password", "password");
        user.put("email", "test@example.com");
        String userJson = objectMapper.writeValueAsString(user);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        // Luego, intenta iniciar sesión con el usuario registrado
        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("username", "testuser");
        loginRequest.put("password", "password2");
        String loginJson = objectMapper.writeValueAsString(loginRequest);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

}

