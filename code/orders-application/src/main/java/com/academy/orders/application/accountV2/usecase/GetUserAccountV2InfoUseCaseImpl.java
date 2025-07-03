package com.academy.orders.application.accountV2.usecase;

import com.academy.orders.domain.account.exception.AccountNotFoundException;
import com.academy.orders.domain.accountv2.entity.AccountV2;
import com.academy.orders.domain.accountv2.repository.AccountV2Repository;
import com.academy.orders.domain.accountv2.usecase.GetUserAccountV2InfoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetUserAccountV2InfoUseCaseImpl implements GetUserAccountV2InfoUseCase {
  private final AccountV2Repository accountV2Repository;

  @Override
  public AccountV2 getUserAccountInfo(Long userId) {
    return accountV2Repository.findAccountById(userId).orElseThrow(() -> new AccountNotFoundException(userId));
  }
}
