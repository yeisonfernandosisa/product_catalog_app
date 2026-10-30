package com.catalog.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.catalog.model.AppUser;
import com.catalog.model.RevokedToken;
import com.catalog.model.Role;
import com.catalog.repository.RevokedTokenRepository;
import com.catalog.security.JwtService;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class TokenRevocationServiceTest {
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
  void revokesTokenAndRemovesExpiredEntries() {
    AppUser user = new AppUser("user@example.com", "User", "hash", Role.USER);
    String token = jwtService.generate(user);

    service.revoke(token, user.getEmail());

    ArgumentCaptor<RevokedToken> revokedToken = ArgumentCaptor.forClass(RevokedToken.class);
    verify(revokedTokens).deleteByExpiresAtBefore(org.mockito.ArgumentMatchers.any());
    verify(revokedTokens).save(revokedToken.capture());
    assertEquals(jwtService.tokenId(token), revokedToken.getValue().getTokenId());
    assertTrue(revokedToken.getValue().getExpiresAt().isAfter(LocalDateTime.now(ZoneOffset.UTC)));
  }

  @Test
  void identifiesRevokedTokenIds() {
    when(revokedTokens.existsById("revoked-token-id")).thenReturn(true);

    assertTrue(service.isRevoked("revoked-token-id"));
  }
}
