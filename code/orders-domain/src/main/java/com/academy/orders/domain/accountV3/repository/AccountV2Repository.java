package com.academy.orders.domain.accountV3.repository;

import com.academy.orders.domain.accountV3.entity.AccountV2;

import java.util.Optional;

public interface AccountV2Repository {
    Optional<AccountV2> findById(Long id);
}
