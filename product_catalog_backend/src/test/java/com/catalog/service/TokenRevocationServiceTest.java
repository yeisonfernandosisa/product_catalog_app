package com.catalog.service;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.catalog.repository.RevokedTokenRepository;
import com.catalog.security.JwtService;

@ExtendWith(MockitoExtension.class)
class TokenRevocationServiceTest {

    @Mock private RevokedTokenRepository revokedTokens;

    private TokenRevocationService service;

    @BeforeEach
    void setUp() {
        service =
                new TokenRevocationService(
                        revokedTokens,
                        new JwtService("test-secret-key-with-at-least-32-characters"));
    }

    @Test
    void returnsTrueWhenTokenWasRevoked() {
        when(revokedTokens.existsById("revoked-token-id")).thenReturn(true);

        assertTrue(service.isRevoked("revoked-token-id"));
    }
}
