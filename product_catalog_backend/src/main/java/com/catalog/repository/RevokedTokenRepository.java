package com.catalog.repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;

import com.catalog.model.RevokedToken;

public interface RevokedTokenRepository extends JpaRepository<RevokedToken, String> {

    void deleteByExpiresAtBefore(LocalDateTime expirationTime);
}
