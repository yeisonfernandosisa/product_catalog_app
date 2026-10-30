package com.catalog.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "revoked_tokens")
public class RevokedToken extends AuditableEntity {

    @Id
    @Column(name = "token_id", length = 36)
    private String tokenId;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    protected RevokedToken() {
    }

    public RevokedToken(String tokenId, LocalDateTime expiresAt, String actor) {
        this.tokenId = tokenId;
        this.expiresAt = expiresAt;
        initializeAudit(actor);
    }

    public String getTokenId() {
        return tokenId;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }
}
