package com.academy.orders.domain.passwordreset.entity.enumerated;

public enum TokenType {
  /** Primary token type for initial password reset requests */
  PRIMARY,
  /** Secondary token type for additional validation or multistep processes */
  SECONDARY
}
