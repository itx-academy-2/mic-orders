package com.academy.orders.domain.accountv2.repository;

import com.academy.orders.domain.accountv2.dto.UpdateUserAccountV2InfoDto;
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

  /**
   * Updates the personal info of the user account with the given ID.
   *
   * @param id the ID of the account to update.
   * @param dto the DTO containing the updated personal information.
   */
  void updateAccountPersonalInfo(Long id, UpdateUserAccountV2InfoDto dto);

  /**
   * Checks if an account exists by its ID.
   *
   * @param id the id of account to check for existence.
   * @return {@code true} if an account with the given id exists, otherwise {@code false}.
   */
  Boolean existsById(Long id);
}
