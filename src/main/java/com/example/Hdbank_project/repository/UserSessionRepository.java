package com.example.Hdbank_project.repository;

import com.example.Hdbank_project.model.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface UserSessionRepository extends JpaRepository<UserSession, Long> {
    List<UserSession> findByUsernameAndLogoutTimeIsNull(String username);
    List<UserSession> findByUsername(String username);
    List<UserSession> findByLogoutTimeIsNull();

}
