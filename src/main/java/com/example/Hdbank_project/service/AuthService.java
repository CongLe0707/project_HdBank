package com.example.Hdbank_project.service;

import com.example.Hdbank_project.dto.RegisterRequest;
import com.example.Hdbank_project.model.Role;
import com.example.Hdbank_project.model.User;
import com.example.Hdbank_project.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User register(RegisterRequest request) throws Exception {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new Exception("Username is already taken");
        }
        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(new HashSet<>(Set.of(Role.ROLE_USER)))  // Chỉ gán ROLE_USER
                .build();

        return userRepository.save(user);
    }

}
