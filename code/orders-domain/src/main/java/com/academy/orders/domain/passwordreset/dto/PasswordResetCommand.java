package com.academy.orders.domain.passwordreset.dto;

public record PasswordResetCommand(String token, String password) {
}
