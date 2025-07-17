package com.academy.orders.application.accountV2.usecase;

import com.academy.orders.domain.account.exception.AccountNotFoundException;
import com.academy.orders.domain.accountv2.dto.UpdateUserAccountV2InfoDto;
import com.academy.orders.domain.accountv2.repository.AccountV2Repository;
import com.academy.orders.domain.accountv2.usecase.UpdateUserAccountV2InfoUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UpdateUserAccountV2InfoUseCaseImpl implements UpdateUserAccountV2InfoUseCase {
  private final AccountV2Repository accountV2Repository;

  @Override
  public void updateUserAccountInfo(Long userId, UpdateUserAccountV2InfoDto updateUserAccountV2InfoDto) {
    if (!accountV2Repository.existsById(userId)) {
      throw new AccountNotFoundException(userId);
    }
    log.info("Updating personal info for user with id {}", userId);
    accountV2Repository.updateAccountPersonalInfo(userId, updateUserAccountV2InfoDto);
  }
}
