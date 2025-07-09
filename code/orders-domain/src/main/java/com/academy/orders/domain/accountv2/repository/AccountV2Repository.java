package com.academy.orders.domain.accountv2.repository;

import com.academy.orders.domain.accountv2.entity.AccountV2;

import java.util.Optional;

public interface AccountV2Repository {
  /**
   * Retrieves an {@link AccountV2} entity by its ID.
   *
   * @param id the ID address of the account to be retrieved.
   *
   * @return an {@link Optional} containing the {@link AccountV2} entity if found, otherwise empty.
   */
  Optional<AccountV2> findAccountById(Long id);
}
