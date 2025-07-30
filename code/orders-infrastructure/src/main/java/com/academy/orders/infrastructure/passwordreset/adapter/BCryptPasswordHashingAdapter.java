package com.academy.orders.infrastructure.passwordreset.adapter;

import com.academy.orders.domain.passwordreset.usecase.PasswordHashingPort;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BCryptPasswordHashingAdapter implements PasswordHashingPort {
  private final PasswordEncoder passwordEncoder;

  @Override
  public String hash(String password) {
    return passwordEncoder.encode(password);
  }
}
