package com.catalog.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.catalog.repository.RevokedTokenRepository;
import com.catalog.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TokenRevocationServiceFailureTest {
  private RevokedTokenRepository revokedTokens;
  private JwtService jwtService;
  private TokenRevocationService service;

  @BeforeEach
  void setUp() {
    revokedTokens = mock(RevokedTokenRepository.class);
    jwtService = new JwtService("test-secret-key-with-at-least-32-characters");
    service = new TokenRevocationService(revokedTokens, jwtService);
  }

  @Test
  void rejectsTokenWhenJwtCannotBeParsed() {
    assertThrows(
        RuntimeException.class, () -> service.revoke("invalid-token", "user@example.com"));
    verify(revokedTokens, never()).save(org.mockito.ArgumentMatchers.any());
  }

  @Test
  void propagatesRepositoryFailureWhenCheckingRevocation() {
    when(revokedTokens.existsById("token-id"))
        .thenThrow(new IllegalStateException("Database unavailable"));

    assertThrows(IllegalStateException.class, () -> service.isRevoked("token-id"));
  }
}
