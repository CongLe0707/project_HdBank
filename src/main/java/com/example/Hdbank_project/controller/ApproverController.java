package com.example.Hdbank_project.controller;

import com.example.Hdbank_project.model.UserSession;
import com.example.Hdbank_project.repository.UserSessionRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
@RestController
@RequestMapping("/api/approver")
public class ApproverController {

    private final UserSessionRepository userSessionRepository;

    public ApproverController(UserSessionRepository userSessionRepository) {
        this.userSessionRepository = userSessionRepository;
    }

    // Lấy lịch sử đăng nhập đăng xuất tất cả người dùng hoặc theo username
    @GetMapping("/sessions")
    public ResponseEntity<?> getUserSessions(@RequestParam(required = false) String username) {
        List<UserSession> sessions;
        if (username == null || username.isEmpty()) {
            sessions = userSessionRepository.findAll();
        } else {
            sessions = userSessionRepository.findByUsername(username);
        }

        // Có thể trả về trực tiếp hoặc map sang DTO để tránh lộ dữ liệu nhạy cảm
        return ResponseEntity.ok(sessions);
    }
}
