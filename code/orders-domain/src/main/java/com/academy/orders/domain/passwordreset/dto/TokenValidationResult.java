package com.academy.orders.domain.passwordreset.dto;

import java.time.Instant;
import java.util.UUID;

public record TokenValidationResult(
    boolean valid,
    UUID token,
    String tokenType,
    Instant expiresAt,
    UUID newToken,
    Instant timestamp) {
}
