package com.example.Hdbank_project.controller;


import com.example.Hdbank_project.config.JwtUtils;
import com.example.Hdbank_project.dto.JwtResponse;
import com.example.Hdbank_project.dto.LoginRequest;
import com.example.Hdbank_project.dto.RegisterRequest;
//import com.example.Hdbank_project.kafka.KafkaLogProducer;
import com.example.Hdbank_project.model.User;
import com.example.Hdbank_project.model.UserSession;
import com.example.Hdbank_project.repository.UserSessionRepository;
import com.example.Hdbank_project.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final AuthService authService;
    private final UserSessionRepository userSessionRepository;
    private final UserDetailsService userDetailsService;
    //private final KafkaLogProducer kafkaLogProducer;


    public AuthController(AuthenticationManager authenticationManager, JwtUtils jwtUtils, AuthService authService, UserSessionRepository userSessionRepository, UserDetailsService userDetailsService, UserSessionRepository loginLogRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.authService = authService;
        this.userSessionRepository = userSessionRepository;
        this.userDetailsService = userDetailsService;
        //this.kafkaLogProducer = kafkaLogProducer;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        // Tạo token
        String accessToken = jwtUtils.generateJwtToken(userDetails);
        String refreshToken = jwtUtils.generateRefreshToken(userDetails);
        // Lưu phiên login
        UserSession session = UserSession.builder()
                .username(userDetails.getUsername())
                .loginTime(LocalDateTime.now())
                .logoutTime(null)
                .build();
        userSessionRepository.save(session);
        // Trả về token
        return ResponseEntity.ok(new JwtResponse(accessToken, refreshToken));
    }



    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body("Missing or invalid Authorization header");
        }
        String token = authHeader.substring(7); // Bỏ "Bearer "

        String username;
        try {
            username = jwtUtils.getUsernameFromJwtToken(token);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid token");
        }
        List<UserSession> sessions = userSessionRepository.findByUsernameAndLogoutTimeIsNull(username);
        if (sessions.isEmpty()) {
            return ResponseEntity.badRequest().body("No active session found for user: " + username);
        }
        sessions.forEach(session -> {
            session.setLogoutTime(LocalDateTime.now());
            userSessionRepository.save(session);
            //kafkaLogProducer.sendLog("Đăng Xuất - User: " + username + " Lúc " + session.getLogoutTime());
        });

        return ResponseEntity.ok("Logout successful for user: " + username);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            System.out.println("RegisterRequest received: " + request);
            User user = authService.register(request);
            System.out.println("User registered: " + user);
            return ResponseEntity.ok("User registered successfully: " + user.getUsername());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        if (refreshToken == null || refreshToken.isEmpty()) {
            return ResponseEntity.badRequest().body("Refresh token is missing");
        }
        if (jwtUtils.validateJwtToken(refreshToken)) {
            try {
                String username = jwtUtils.getUsernameFromJwtToken(refreshToken);
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                String newAccessToken = jwtUtils.generateJwtToken(userDetails);
                String newRefreshToken = jwtUtils.generateRefreshToken(userDetails);
                return ResponseEntity.ok(new JwtResponse(newAccessToken, newRefreshToken));
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired refresh token");
        }
    }
}

