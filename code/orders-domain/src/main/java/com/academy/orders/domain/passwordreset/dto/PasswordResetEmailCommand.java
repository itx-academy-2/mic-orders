package com.academy.orders.domain.passwordreset.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Command for initiating a password reset email request.
 *
 * @param email the email address requesting the reset
 * @param ipAddress the client IP address
 */
public record PasswordResetEmailCommand(
    @NotBlank String email,
    @NotBlank String ipAddress) {
}
