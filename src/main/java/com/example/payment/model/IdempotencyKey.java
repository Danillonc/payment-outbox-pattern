package com.example.payment.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "idempotency_keys")
public class IdempotencyKey {

    @Id
    private UUID key;

    private Instant createdAt;

    public IdempotencyKey() {
    }

    public IdempotencyKey(UUID key) {
        this.key = key;
        this.createdAt = Instant.now();
    }

    public UUID getKey() {
        return key;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}