package com.academy.orders.domain.account.exception;

import lombok.Getter;

@Getter
public class ConcurrentUpdateException extends RuntimeException {
  public ConcurrentUpdateException(String message, Throwable cause) {
    super(message, cause);
  }
}
