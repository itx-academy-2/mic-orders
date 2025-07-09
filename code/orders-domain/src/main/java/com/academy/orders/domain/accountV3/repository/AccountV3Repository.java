package com.academy.orders.domain.accountV3.repository;

import com.academy.orders.domain.accountV3.entity.AccountV3;

import java.util.Optional;

public interface AccountV3Repository {
    Optional<AccountV3> findById(Long id);
}
