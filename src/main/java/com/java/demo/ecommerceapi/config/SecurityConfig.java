package com.java.demo.ecommerceapi.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.java.demo.ecommerceapi.service.IUserService;

import org.springframework.security.authentication.dao.DaoAuthenticationProvider;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final IUserService userService;
    private final JwtFilter jwtFilter;
    private final PasswordEncoder passwordEncoder;
    
 @Autowired
    public SecurityConfig( @Lazy IUserService userService, JwtFilter jwtFilter, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.jwtFilter = jwtFilter;
        this.passwordEncoder = passwordEncoder; 
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            return userService.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        };
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
       authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .authenticationProvider(authenticationProvider())
                .build();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/users/register", "/api/auth/login").permitAll() // Permitir registro y login
//                        .requestMatchers("/api/products/**").permitAll() // Permitir acceso a productos sin autenticación
  //                      .requestMatchers("/api/brands/**").permitAll()
    //                    .requestMatchers("/api/categories/**").permitAll()
      //                  .requestMatchers("/api/stocks/**").authenticated()
        //                .requestMatchers("/api/orders/**").authenticated() // Requiere autenticación para las órdenes   
          //              .requestMatchers("/api/admin/**").hasRole("ADMIN") // Solo los administradores pueden acceder a /api/admin/**                        
            //            .anyRequest().authenticated() // Requiere autenticación para cualquier otra ruta
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class); // Agrega el filtro JWT antes del filtro de autenticación
        return http.build();
    }
}
