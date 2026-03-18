package com.htc.incidentmanagement.service;

public interface AuthTokenService {

    void saveToken(Long userId, String token, long expiryMillis);

    boolean isTokenValid(String token);

    void revokeToken(String token);

    void revokeAllTokensForUser(Long userId);
}
