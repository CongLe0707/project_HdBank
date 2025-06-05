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
            if (userRepository.existsByEmail(request.getUsername())) {
                throw new Exception("Email đã tồn tại trong hệ thống");
            }

            User user = User.builder()
                    .email(request.getUsername())
                    .phoneNumber(request.getPhoneNumber())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .roles(Set.of(Role.ROLE_USER))
                    .build();

            return userRepository.save(user);
        }

    }
