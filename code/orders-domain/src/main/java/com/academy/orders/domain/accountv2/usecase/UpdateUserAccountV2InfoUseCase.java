package com.academy.orders.domain.accountv2.usecase;

import com.academy.orders.domain.accountv2.dto.UpdateUserAccountV2InfoDto;

/**
 * Use case interface for updating personal info for user account.
 */
public interface UpdateUserAccountV2InfoUseCase {

  /**
   * Updates personal info for user account.
   *
   * @param userId the {@link String} user's id.
   * @param updateUserAccountV2InfoDto {@link UpdateUserAccountV2InfoDto} the DTO containing updated personal info
   */
  void updateUserAccountInfo(Long userId, UpdateUserAccountV2InfoDto updateUserAccountV2InfoDto);
}
