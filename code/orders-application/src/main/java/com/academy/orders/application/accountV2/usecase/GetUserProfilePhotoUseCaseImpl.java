package com.academy.orders.application.accountV2.usecase;

import com.academy.orders.domain.account.exception.AccountNotFoundException;
import com.academy.orders.domain.accountv2.entity.AccountV2;
import com.academy.orders.domain.accountv2.repository.AccountV2Repository;
import com.academy.orders.domain.accountv2.usecase.GetUserProfilePhotoUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class GetUserProfilePhotoUseCaseImpl implements GetUserProfilePhotoUseCase {
  private final AccountV2Repository accountV2Repository;

  @Override
  public String getProfilePhoto(Long userId) {
    log.debug("Fetching account for user {}", userId);
    AccountV2 account = accountV2Repository.findAccountById(userId)
        .orElseThrow(() -> new AccountNotFoundException(userId));

    String photo = account.photo();
    log.info("Returning profile photo for user: {}", userId);
    return photo;
  }
}
