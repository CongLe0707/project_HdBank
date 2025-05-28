package com.example.Hdbank_project.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import com.example.Hdbank_project.model.User; // ✅ Your entity class


import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
}