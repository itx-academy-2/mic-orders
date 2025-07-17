package com.academy.orders.domain.account.exception;

import com.academy.orders.domain.common.exception.NotFoundException;
import lombok.Getter;

@Getter
public class AccountNotFoundException extends NotFoundException {
  private final Long accountId;

  private final String email;

  /**
   * Constructs an AccountNotFoundException for a missing account identified by its ID.
   *
   * @param accountId the ID of the account that was not found
   */
  public AccountNotFoundException(Long accountId) {
    super(String.format("Account with id: %d is not found", accountId));
    this.accountId = accountId;
    this.email = null;
  }

  /**
   * Constructs an AccountNotFoundException for a missing account identified by email.
   *
   * @param email the email address of the account that was not found
   */
  public AccountNotFoundException(String email) {
    super(String.format("Account with email: %s is not found", email));
    this.email = email;
    this.accountId = null;
  }
}
