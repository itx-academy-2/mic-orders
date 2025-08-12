package com.academy.orders.application.accountV2.usecase;

import com.academy.orders.domain.account.exception.AccountNotFoundException;
import com.academy.orders.domain.accountv2.repository.AccountV2Repository;
import com.academy.orders.domain.accountv2.usecase.DeleteUserProfilePhotoUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class DeleteUserProfilePhotoUseCaseImpl implements DeleteUserProfilePhotoUseCase {
  private final AccountV2Repository accountV2Repository;

  @Override
  public void deleteProfilePhoto(Long userId) {
    log.debug("Checking existence of account for user {}", userId);
    if (!accountV2Repository.existsById(userId)) {
      log.warn("Account not found for user {}", userId);
      throw new AccountNotFoundException(userId);
    }
    log.info("Deleting profile photo for user {}", userId);
    accountV2Repository.updateAccountPhoto(userId, null);
  }
}
