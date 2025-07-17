package com.academy.orders.infrastructure.passwordreset.repository;

import com.academy.orders.domain.passwordreset.usecase.PasswordHashingPort;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BCryptPasswordHashingAdapter implements PasswordHashingPort {
  private final PasswordEncoder passwordEncoder;

  /**
   * Returns a hashed representation of the provided plain text password.
   *
   * @param password the plain text password to hash
   * @return the hashed password string
   */
  @Override
  public String hash(String password) {
    return passwordEncoder.encode(password);
  }
}
