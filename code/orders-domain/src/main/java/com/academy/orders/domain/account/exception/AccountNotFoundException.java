package com.academy.orders.domain.account.exception;

import com.academy.orders.domain.common.exception.NotFoundException;
import lombok.Getter;

@Getter
public class AccountNotFoundException extends NotFoundException {
  private final Long accountId;

  private final String email;

  public AccountNotFoundException(Long accountId) {
    super(String.format("Account with id: %d is not found", accountId));
    this.accountId = accountId;
    this.email = null;
  }

  public AccountNotFoundException(String email) {
    super(String.format("Account with email: %s is not found", email));
    this.email = email;
    this.accountId = null;
  }
}
