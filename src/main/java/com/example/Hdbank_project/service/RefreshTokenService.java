package com.example.Hdbank_project.service;

import com.example.Hdbank_project.model.RefreshToken;
import com.example.Hdbank_project.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshToken createToken(String username) {
        String token = UUID.randomUUID().toString();
        LocalDateTime expiry = LocalDateTime.now().plusDays(7); // 7 ngày
        RefreshToken refreshToken = RefreshToken.builder()
                .token(token)
                .username(username)
                .expiryDate(expiry)
                .revoked(false)
                .build();
        return refreshTokenRepository.save(refreshToken);
    }

    public Optional<RefreshToken> validateToken(String token) {
        return refreshTokenRepository.findByToken(token)
                .filter(rt -> !rt.isRevoked() && rt.getExpiryDate().isAfter(LocalDateTime.now()));
    }

    public void revokeToken(String token) {
        refreshTokenRepository.findByToken(token).ifPresent(rt -> {
            rt.setRevoked(true);
            refreshTokenRepository.save(rt);
        });
    }

    public void revokeAllUserTokens(String username) {
        refreshTokenRepository.deleteByUsername(username); // hoặc cập nhật `revoked = true` toàn bộ token của user
    }
}
