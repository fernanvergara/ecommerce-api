package com.java.demo.ecommerceapi.config;

import com.java.demo.ecommerceapi.model.Role;
import com.java.demo.ecommerceapi.model.User;
import com.java.demo.ecommerceapi.repository.RoleRepository;
import com.java.demo.ecommerceapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class UserInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserInitializer(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.findByUsername("admin").isEmpty()) {
            User adminUser = new User();
            adminUser.setUsername("admin");
            adminUser.setPassword(passwordEncoder.encode("12345"));
            adminUser.setEmail("admin@example.com"); // Cambiar email por si es necesario

            Optional<Role> adminRoleOptional = roleRepository.findByName("ROLE_ADMIN");
            Role adminRole;
            if (adminRoleOptional.isPresent()) {
                adminRole = adminRoleOptional.get();
            } else {
                adminRole = new Role();
                adminRole.setName("ROLE_ADMIN");
                roleRepository.save(adminRole);
            }

            adminUser.setRoles(List.of(adminRole));
            userRepository.save(adminUser);
            System.out.println("Usuario 'admin' creado con rol 'ROLE_ADMIN'.");
        } else {
            System.out.println("El usuario 'admin' ya existe en la base de datos.");
        }
    }
}