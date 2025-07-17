package com.academy.orders.domain.accountv2.dto;

import lombok.Builder;

@Builder
public record UpdateUserAccountV2InfoDto(
    String firstName,
    String lastName,
    String phone) {
}
