package com.catalog.service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.catalog.model.RevokedToken;
import com.catalog.repository.RevokedTokenRepository;
import com.catalog.security.JwtService;

@Service
public class TokenRevocationService {

    private final RevokedTokenRepository revokedTokens;
    private final JwtService jwtService;

    public TokenRevocationService(RevokedTokenRepository revokedTokens, JwtService jwtService) {
        this.revokedTokens = revokedTokens;
        this.jwtService = jwtService;
    }

    @Transactional
    public void revoke(String token, String actor) {
        String tokenId = jwtService.tokenId(token);
        LocalDateTime expiresAt
                = LocalDateTime.ofInstant(jwtService.expiration(token).toInstant(), ZoneOffset.UTC);

        revokedTokens.deleteByExpiresAtBefore(LocalDateTime.now(ZoneOffset.UTC));
        revokedTokens.save(new RevokedToken(tokenId, expiresAt, actor));
    }

    @Transactional(readOnly = true)
    public boolean isRevoked(String tokenId) {
        return revokedTokens.existsById(tokenId);
    }
}
