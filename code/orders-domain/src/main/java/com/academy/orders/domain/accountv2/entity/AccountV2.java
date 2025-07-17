package com.academy.orders.domain.accountv2.entity;

import com.academy.orders.domain.account.entity.enumerated.Role;
import com.academy.orders.domain.account.entity.enumerated.UserStatus;
import lombok.Builder;
import java.time.LocalDateTime;

@Builder
public record AccountV2(Long id, String password, String email, String firstName, String lastName,
    String phone, String photo, Role role, UserStatus status, LocalDateTime createdAt) {
}
