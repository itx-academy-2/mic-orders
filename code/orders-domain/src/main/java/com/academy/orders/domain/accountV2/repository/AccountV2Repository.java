package com.academy.orders.domain.accountV2.repository;

import com.academy.orders.domain.accountV2.entity.AccountV2;

import java.util.Optional;

public interface AccountV2Repository {
    Optional<AccountV2> findById(Long id);
}
