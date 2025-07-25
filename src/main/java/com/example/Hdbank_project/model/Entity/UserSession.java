package com.example.Hdbank_project.model.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private LocalDateTime loginTime;

    private LocalDateTime logoutTime;

    public long getSessionDurationSeconds() {
        if (logoutTime == null) {
            return java.time.Duration.between(loginTime, LocalDateTime.now()).getSeconds();
        } else {
            return java.time.Duration.between(loginTime, logoutTime).getSeconds();
        }
    }
}
