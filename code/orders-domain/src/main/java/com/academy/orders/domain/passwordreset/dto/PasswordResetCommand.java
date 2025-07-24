package com.academy.orders.domain.passwordreset.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PasswordResetCommand(
    @NotBlank(message = "Token cannot be blank") String token,

    @NotBlank(message = "Password cannot be blank") @Size(min = 8, max = 64,
        message = "Password must be between 8 and 64 characters") String password) {
}
