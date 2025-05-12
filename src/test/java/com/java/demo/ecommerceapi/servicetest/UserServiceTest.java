package com.java.demo.ecommerceapi.servicetest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.java.demo.ecommerceapi.model.Role;
import com.java.demo.ecommerceapi.model.User;
import com.java.demo.ecommerceapi.repository.RoleRepository;
import com.java.demo.ecommerceapi.repository.UserRepository;
import com.java.demo.ecommerceapi.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testRegisterUser() {
        // Arrange
        String username = "testuser";
        String password = "password";
        String encodedPassword = "encodedPassword";
        String email = "test@example.com";
        Role userRole = new Role(1L, "ROLE_USER", null);
        User savedUser = new User(1L, username, email, encodedPassword, List.of(userRole), null);

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(userRole));
        when(passwordEncoder.encode(password)).thenReturn(encodedPassword);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // Act
        User result = userService.registerUser(username, password, email);

        // Assert
        assertEquals(1L, result.getId());
        assertEquals(username, result.getUsername());
        assertEquals(encodedPassword, result.getPassword());
        assertEquals(email, result.getEmail());
        assertEquals(1, result.getRoles().size());
        assertEquals("ROLE_USER", result.getRoles().get(0).getName());
        verify(userRepository, times(1)).findByUsername(username);
        verify(userRepository, times(1)).findByEmail(email);
        verify(roleRepository, times(1)).findByName("ROLE_USER");
        verify(passwordEncoder, times(1)).encode(password);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void testRegisterUser_UsernameExists() {
        // Arrange
        String username = "testuser";
        String password = "password";
        String email = "test@example.com";
        User existingUser = new User(1L, username, "encodedPassword", email, new ArrayList<>(), null);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(existingUser));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.registerUser(username, password, email);
        });
        assertEquals("Username 'testuser' already exists", exception.getMessage());
        verify(userRepository, times(1)).findByUsername(username);
        verify(userRepository, times(0)).findByEmail(anyString());
        verify(roleRepository, times(0)).findByName(anyString());
        verify(passwordEncoder, times(0)).encode(anyString());
        verify(userRepository, times(0)).save(any(User.class));
    }

    @Test
    public void testRegisterUser_EmailExists() {
        // Arrange
        String username = "testuser";
        String password = "password";
        String email = "test@example.com";
        User existingUser = new User(1L, "otheruser", "encodedPassword", email, new ArrayList<>(), null);

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(existingUser));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.registerUser(username, password, email);
        });
        assertEquals("Email 'test@example.com' already exists", exception.getMessage());
        verify(userRepository, times(1)).findByUsername(username);
        verify(userRepository, times(1)).findByEmail(email);
        verify(roleRepository, times(0)).findByName(anyString());
        verify(passwordEncoder, times(0)).encode(anyString());
        verify(userRepository, times(0)).save(any(User.class));
    }

    @Test
    public void testFindByUsername() {
        // Arrange
        String username = "testuser";
        User user = new User(1L, username, "encodedPassword", "test@example.com", new ArrayList<>(), null);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        // Act
        Optional<User> result = userService.findByUsername(username);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(username, result.get().getUsername());
        verify(userRepository, times(1)).findByUsername(username);
    }
}

