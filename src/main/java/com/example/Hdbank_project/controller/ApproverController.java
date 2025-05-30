package com.example.Hdbank_project.controller;

import com.example.Hdbank_project.model.UserSession;
import com.example.Hdbank_project.repository.UserSessionRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Set;
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
        return ResponseEntity.ok(sessions);
    }
    @GetMapping("/active-users")
    public ResponseEntity<?> getActiveUsers() {
        List<UserSession> activeSessions = userSessionRepository.findByLogoutTimeIsNull();

        // Lấy danh sách username duy nhất
        Set<String> onlineUsers = activeSessions.stream()
                .map(UserSession::getUsername)
                .collect(Collectors.toSet());

        return ResponseEntity.ok(Map.of(
                "Số lượng user đang online", onlineUsers.size(),
                "Danh sách các user đang online", onlineUsers
        ));
    }
}
