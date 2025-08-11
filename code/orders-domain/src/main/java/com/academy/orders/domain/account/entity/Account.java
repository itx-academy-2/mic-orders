package com.academy.orders.domain.account.entity;

import com.academy.orders.domain.account.entity.enumerated.Role;
import com.academy.orders.domain.account.entity.enumerated.UserStatus;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record Account(Long id, String password, String email, String firstName, String lastName, Role role,
    UserStatus status, LocalDateTime createdAt) {
  public Account withPassword(String newPassword) {
    return new Account(
        this.id,
        newPassword,
        this.email,
        this.firstName,
        this.lastName,
        this.role,
        this.status,
        this.createdAt);
  }
}
