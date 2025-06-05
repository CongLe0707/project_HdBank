package com.example.Hdbank_project.controller;

import com.example.Hdbank_project.config.JwtUtils;
import com.example.Hdbank_project.dto.JwtResponse;
import com.example.Hdbank_project.dto.LoginRequest;
import com.example.Hdbank_project.dto.RegisterRequest;
import com.example.Hdbank_project.model.User;
import com.example.Hdbank_project.model.UserSession;
import com.example.Hdbank_project.repository.UserSessionRepository;
import com.example.Hdbank_project.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final AuthService authService;
    private final UserSessionRepository userSessionRepository;
    private final UserDetailsService userDetailsService;

    public AuthController(AuthenticationManager authenticationManager, JwtUtils jwtUtils,
                          AuthService authService, UserSessionRepository userSessionRepository,
                          UserDetailsService userDetailsService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.authService = authService;
        this.userSessionRepository = userSessionRepository;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest, BindingResult result) {
        if (result.hasErrors()) {
            String errorMessage = result.getFieldErrors().stream()
                    .map(err -> err.getDefaultMessage())
                    .collect(Collectors.joining("; "));
            return ResponseEntity.badRequest().body(Map.of("message", errorMessage));
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String accessToken = jwtUtils.generateJwtToken(userDetails);
            String refreshToken = jwtUtils.generateRefreshToken(userDetails);

            UserSession session = UserSession.builder()
                    .username(userDetails.getUsername())
                    .loginTime(LocalDateTime.now())
                    .logoutTime(null)
                    .build();
            userSessionRepository.save(session);

            JwtResponse jwtResponse = new JwtResponse(accessToken, refreshToken);

            return ResponseEntity.ok(Map.of(
                    "message", "Đăng nhập thành công",
                    "data", jwtResponse
            ));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "message", "Tên đăng nhập hoặc mật khẩu không đúng"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "message", "Đã xảy ra lỗi trong quá trình đăng nhập"
            ));
        }
    }


    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Thiếu hoặc sai định dạng Authorization header"
            ));
        }
        String token = authHeader.substring(7);
        try {
            String username = jwtUtils.getUsernameFromJwtToken(token);
            List<UserSession> sessions = userSessionRepository.findByUsernameAndLogoutTimeIsNull(username);
            if (sessions.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "message", "Không tìm thấy phiên hoạt động của người dùng: " + username
                ));
            }
            sessions.forEach(session -> {
                session.setLogoutTime(LocalDateTime.now());
                userSessionRepository.save(session);
            });
            return ResponseEntity.ok(Map.of(
                    "message", "Đăng xuất thành công",
                    "data", Map.of("username", username)
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Token không hợp lệ"
            ));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request, BindingResult result) {
        if (result.hasErrors()) {
            String errorMessage = result.getFieldErrors().stream()
                    .map(err -> err.getDefaultMessage())
                    .collect(Collectors.joining("; "));
            return ResponseEntity.badRequest().body(Map.of("message", errorMessage));
        }

        try {
            User user = authService.register(request);
            return ResponseEntity.ok(Map.of(
                    "message", "Đăng ký thành công",
                    "data", Map.of("username", user.getUsername())
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", e.getMessage()
            ));
        }
    }


    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        if (refreshToken == null || refreshToken.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Thiếu refresh token"
            ));
        }
        if (jwtUtils.validateJwtToken(refreshToken)) {
            try {
                String username = jwtUtils.getUsernameFromJwtToken(refreshToken);
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                String newAccessToken = jwtUtils.generateJwtToken(userDetails);
                String newRefreshToken = jwtUtils.generateRefreshToken(userDetails);
                JwtResponse jwtResponse = new JwtResponse(newAccessToken, newRefreshToken);
                return ResponseEntity.ok(Map.of(
                        "message", "Làm mới token thành công",
                        "data", jwtResponse
                ));
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                        "message", "Refresh token không hợp lệ"
                ));
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "message", "Refresh token không hợp lệ hoặc đã hết hạn"
            ));
        }
    }
}
