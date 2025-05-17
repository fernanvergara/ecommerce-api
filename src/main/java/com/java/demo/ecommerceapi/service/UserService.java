package com.java.demo.ecommerceapi.service;

import com.java.demo.ecommerceapi.exception.UserAlreadyExistsException;
import com.java.demo.ecommerceapi.model.Role;
import com.java.demo.ecommerceapi.model.User;
import com.java.demo.ecommerceapi.repository.RoleRepository;
import com.java.demo.ecommerceapi.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements IUserService, UserDetailsService{
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerUser(String username, String password, String email) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new UserAlreadyExistsException("Username '"+username+"' already exists");
        }
        if (userRepository.findByEmail(email).isPresent()) {
            throw new UserAlreadyExistsException("Email '"+email+"' already exists");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setEmail(email);
        // Asignar rol por defecto
        if((roleRepository.findByName("ROLE_USER")).isEmpty()){
            roleRepository.save(new Role(null, "ROLE_USER", null));
        }
        Optional<Role> userRole = roleRepository.findByName("ROLE_USER");
//                .orElseThrow(() -> new ObjectNotFoundException("Role 'ROLE_USER' not found"));
        user.setRoles(List.of(userRole.get()));
        return userRepository.save(user);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override  
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
