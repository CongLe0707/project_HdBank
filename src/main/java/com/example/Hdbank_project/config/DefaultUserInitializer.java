package com.example.Hdbank_project.config;

import com.example.Hdbank_project.model.Enum.Role;
import com.example.Hdbank_project.model.Entity.User;
import com.example.Hdbank_project.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class DefaultUserInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    public void initApproverUser() {
        String approverEmail = "cong123@hdbank.vn";
        if (!userRepository.existsByEmail(approverEmail)) {
            User approver = User.builder()
                    .email(approverEmail)
                    .phoneNumber("0901234567")
                    .password(passwordEncoder.encode("Cong123@"))
                    .roles(Set.of(Role.ROLE_APPROVER))
                    .build();
            userRepository.save(approver);
        }
    }

}
