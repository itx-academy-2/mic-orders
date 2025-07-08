package com.academy.orders.domain.accountV2.entity;

import com.academy.orders.domain.account.entity.enumerated.Role;
import com.academy.orders.domain.account.entity.enumerated.UserStatus;
import com.academy.orders.domain.postaddress.entity.PostAddressV2;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record AccountV2(Long id, String password, String email, String firstName, String lastName, Role role,
                        UserStatus status, LocalDateTime createdAt, List<PostAddressV2> postAddresses) {
}
