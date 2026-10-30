package com.catalog.repository;

import com.catalog.model.RevokedToken;
import java.time.LocalDateTime;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RevokedTokenRepository extends JpaRepository<RevokedToken, String> {
  void deleteByExpiresAtBefore(LocalDateTime expirationTime);
}
