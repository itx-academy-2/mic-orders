package com.academy.orders.application.account.usecase;

import com.academy.orders.domain.account.entity.enumerated.UserStatus;
import com.academy.orders.domain.account.exception.AccountNotFoundException;
import com.academy.orders.domain.account.repository.AccountRepository;
import com.academy.orders.domain.account.usecase.ChangeAccountStatusUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChangeAccountStatusUseCaseImpl implements ChangeAccountStatusUseCase {
  private final AccountRepository accountRepository;

  @Override
  @Transactional
  public void changeStatus(Long id, UserStatus status) {
    checkAccountExistsById(id);
    updateStatus(id, status);
  }

  private void checkAccountExistsById(Long id) {
    if (Boolean.FALSE.equals(accountRepository.existsById(id))) {
      throw new AccountNotFoundException(id);
    }
  }

  private void updateStatus(Long id, UserStatus status) {
    log.debug("Updating account status to {} for account with id {}", status, id);
    accountRepository.updateStatus(id, status);
  }
}
