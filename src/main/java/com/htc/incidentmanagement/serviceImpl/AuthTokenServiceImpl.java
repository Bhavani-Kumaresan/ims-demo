package com.htc.incidentmanagement.serviceImpl;

import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.htc.incidentmanagement.model.AuthToken;
import com.htc.incidentmanagement.repository.AuthTokenRepository;
import com.htc.incidentmanagement.service.AuthTokenService;

@Service
public class AuthTokenServiceImpl implements AuthTokenService {

    private static final Logger logger = LoggerFactory.getLogger(AuthTokenServiceImpl.class);

    private final AuthTokenRepository authTokenRepository;

    public AuthTokenServiceImpl(AuthTokenRepository authTokenRepository) {
        this.authTokenRepository = authTokenRepository;
        logger.info("AuthTokenService initialized.");
    }

    @Override
    public void saveToken(Long userId, String token, long expiryMillis) {

        AuthToken authToken = new AuthToken();
        authToken.setUserId(userId);
        authToken.setToken(token);
        authToken.setCreatedAt(LocalDateTime.now());
        authToken.setExpiresAt(
                LocalDateTime.now().plusSeconds(expiryMillis / 1000));
        authToken.setIsExpired(false);
        authToken.setIsRevoked(false);

        authTokenRepository.save(authToken);
    }

    @Override
    public boolean isTokenValid(String token) {

        return authTokenRepository
                .findByTokenAndIsExpiredFalseAndIsRevokedFalse(token)
                .filter(t -> {
                    if (t.getExpiresAt().isBefore(LocalDateTime.now())) {
                        t.setIsExpired(true);
                        authTokenRepository.save(t);
                        return false;
                    }
                    return true;
                })
                .isPresent();
    }

    @Override
    @Transactional
    public void revokeToken(String token) {

        AuthToken authToken = authTokenRepository
                .findByTokenAndIsExpiredFalseAndIsRevokedFalse(token)
                .orElseThrow(() -> new RuntimeException("Invalid or expired token"));

        authToken.setIsRevoked(true);
        authTokenRepository.save(authToken);
    }

    @Override
    @Transactional
    public void revokeAllTokensForUser(Long userId) {
        authTokenRepository.deleteAllByUserId(userId);
    }
}
