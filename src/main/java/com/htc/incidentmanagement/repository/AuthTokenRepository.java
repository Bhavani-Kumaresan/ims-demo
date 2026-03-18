package com.htc.incidentmanagement.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.htc.incidentmanagement.model.AuthToken;

public interface AuthTokenRepository extends JpaRepository<AuthToken, Long> {

    Optional<AuthToken> findByTokenAndIsExpiredFalseAndIsRevokedFalse(String token);

    void deleteAllByUserId(Long userId);

    @Modifying
    @Query("UPDATE AuthToken t SET t.isRevoked = true, t.isExpired = true WHERE t.userId = :userId")
    void revokeAllTokensByUserId(Long userId);

    Optional<AuthToken> findByTokenAndIsRevokedFalseAndIsExpiredFalse(String token);

    Optional<AuthToken> findTopByUserIdOrderByCreatedAtDesc(Long userId);

     Optional<AuthToken> findByToken(String token);
}
