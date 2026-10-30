package com.catalog.security;

import com.catalog.model.AppUser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {
  private final SecretKey key;

  public JwtService(@Value("${app.jwt.secret}") String secret) {
    this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
  }

  public String generate(AppUser user) {
    long now = System.currentTimeMillis();
    return Jwts.builder()
        .id(UUID.randomUUID().toString())
        .subject(user.getEmail())
        .claim("role", user.getRole().name())
        .issuedAt(new Date(now))
        .expiration(new Date(now + 86400000))
        .signWith(key)
        .compact();
  }

  public String subject(String token) {
    return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().getSubject();
  }

  public String tokenId(String token) {
    String tokenId =
        Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().getId();
    return tokenId != null
        ? tokenId
        : UUID.nameUUIDFromBytes(token.getBytes(StandardCharsets.UTF_8)).toString();
  }

  public Date expiration(String token) {
    return Jwts.parser()
        .verifyWith(key)
        .build()
        .parseSignedClaims(token)
        .getPayload()
        .getExpiration();
  }

  public boolean isValid(String token) {
    try {
      Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
      return true;
    } catch (io.jsonwebtoken.JwtException | IllegalArgumentException exception) {
      return false;
    }
  }
}
