package com.java.demo.ecommerceapi.configtest;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import com.java.demo.ecommerceapi.config.JwtFilter;
import com.java.demo.ecommerceapi.config.JwtService;
import com.java.demo.ecommerceapi.model.User;
import com.java.demo.ecommerceapi.service.IUserService;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class JwtFilterTest {

    @Mock
    private IUserService userService;
    @Mock
    private JwtService jwtService;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private FilterChain filterChain;
    @Mock
    private User userDetails;

    @InjectMocks
    private JwtFilter jwtFilter;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.clearContext(); // Limpiar el contexto de seguridad antes de cada prueba
    }

    @Test
    public void testDoFilterInternal_NoAuthHeader() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn(null);

        // Act
        jwtFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, times(1)).doFilter(request, response);
        verifyNoInteractions(jwtService, userService);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    public void testDoFilterInternal_InvalidAuthHeader() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("Invalid");

        // Act
        jwtFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, times(1)).doFilter(request, response);
        verifyNoInteractions(jwtService, userService);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    public void testDoFilterInternal_ValidAuthHeader_InvalidToken() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("Bearer token");
        when(jwtService.extractUsername("token")).thenReturn("username");
        when(userService.findByUsername("username")).thenReturn(Optional.of(userDetails));
        when(jwtService.isTokenValid("token", userDetails)).thenReturn(false);

        // Act
        jwtFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, times(1)).doFilter(request, response);
        verify(jwtService, times(1)).extractUsername("token");
        verify(userService, times(1)).findByUsername("username");
        verify(jwtService, times(1)).isTokenValid("token", userDetails);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    public void testDoFilterInternal_ValidAuthHeader_ValidToken() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("Bearer valid_token");
        when(jwtService.extractUsername("valid_token")).thenReturn("username");
        when(userService.findByUsername("username")).thenReturn(Optional.of(userDetails));
        when(jwtService.isTokenValid("valid_token", userDetails)).thenReturn(true);
        when(userDetails.getAuthorities()).thenReturn(Collections.emptyList());

        // Act
        jwtFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, times(1)).doFilter(request, response);
        verify(jwtService, times(1)).extractUsername("valid_token");
        verify(userService, times(1)).findByUsername("username");
        verify(jwtService, times(1)).isTokenValid("valid_token", userDetails);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication);
        assertEquals("userDetails", authentication.getPrincipal().toString());
//        assertEquals(userDetails, authentication.getCredentials());
        assertTrue(authentication.getAuthorities().isEmpty());
        verify(request, times(1)).getHeader("Authorization");
    }

    @Test
    public void testDoFilterInternal_ValidAuthHeader_ValidToken_AuthenticationAlreadyExists() throws ServletException, IOException{
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("Bearer valid_token");
        when(jwtService.extractUsername("valid_token")).thenReturn("username");
        UserDetails existingUserDetails = mock(UserDetails.class);
        UsernamePasswordAuthenticationToken existingAuthToken = new UsernamePasswordAuthenticationToken(existingUserDetails, null, existingUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(existingAuthToken);

        // Act
        jwtFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, times(1)).doFilter(request, response);
        verify(jwtService, times(1)).extractUsername("valid_token");
        verifyNoInteractions(userService);
//        verifyNoInteractions(jwtService, times(0)).isTokenValid(anyString(), any());
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(existingAuthToken, SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    public void testDoFilterInternal_UserNotFound() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("Bearer token");
        when(jwtService.extractUsername("token")).thenReturn("non_existent_user");
        when(userService.findByUsername("non_existent_user")).thenReturn(Optional.empty());

        // Act
        jwtFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, times(1)).doFilter(request, response);
        verify(jwtService, times(1)).extractUsername("token");
        verify(userService, times(1)).findByUsername("non_existent_user");
//        verifyNoInteractions(jwtService, times(0)).isTokenValid(anyString(), any());
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}