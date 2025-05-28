package com.example.Hdbank_project.config;

import com.example.Hdbank_project.model.Role;
import com.example.Hdbank_project.model.User;
import com.example.Hdbank_project.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DefaultUserInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    public void initApproverUser() {
        String approverUsername = "cong123";
        if (!userRepository.existsByUsername(approverUsername)) {
            User approver = User.builder()
                    .username(approverUsername)
                    .password(passwordEncoder.encode("1234"))
                    .roles(new HashSet<>(Set.of(Role.ROLE_APPROVER)))
                    .build();
            userRepository.save(approver);
            System.out.println("✅ Approver account created: username=approver, password=Test@123");
        }
    }
}
