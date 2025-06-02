package com.example.Hdbank_project.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "RefreshToken")
public class RefreshToken {
    @Id
    private String token;
    private  String username;
    private LocalDateTime expiryDate;
    private boolean revoked;
}
