package com.example.Hdbank_project.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import com.example.Hdbank_project.model.Entity.User; // ✅ Your entity class


import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);

}