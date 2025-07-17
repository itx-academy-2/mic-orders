package com.academy.orders.domain.accountv2.usecase;

import com.academy.orders.domain.accountv2.entity.AccountV2;

/**
 * Use case interface for getting personal info for user account.
 */
public interface GetUserAccountV2InfoUseCase {

  /**
   * Retrieves a main info about user.
   *
   * @param userId the {@link String} user's id.
   *
   * @return an {@link AccountV2} object with user info such as Name, Surname, email etc.
   */
  AccountV2 getUserAccountInfo(Long userId);
}
