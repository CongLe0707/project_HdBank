package com.example.Hdbank_project.controller;

import com.example.Hdbank_project.model.Entity.UserSession;
import com.example.Hdbank_project.repository.UserSessionRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/approver")
public class ApproverController {

    private final UserSessionRepository userSessionRepository;

    public ApproverController(UserSessionRepository userSessionRepository) {
        this.userSessionRepository = userSessionRepository;
    }
    @GetMapping("/sessions")
    public ResponseEntity<?> getUserSessions(@RequestParam(required = false) String username) {
        List<UserSession> sessions;
        if (username == null || username.isEmpty()) {
            sessions = userSessionRepository.findAll();
        } else {
            sessions = userSessionRepository.findByUsername(username);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Lấy danh sách phiên làm việc thành công");
        response.put("data", sessions);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/active-users")
    public ResponseEntity<?> getActiveUsers() {
        List<UserSession> activeSessions = userSessionRepository.findByLogoutTimeIsNull();
        Set<String> onlineUsers = activeSessions.stream()
                .map(UserSession::getUsername)
                .collect(Collectors.toSet());
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Lấy danh sách người dùng đang online thành công");
        response.put("total", onlineUsers.size());
        response.put("data", onlineUsers);
        return ResponseEntity.ok(response);
    }
}
