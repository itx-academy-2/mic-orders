package com.academy.orders.apirest.auth.validator;

import com.academy.orders.apirest.auth.util.SecurityUtils;
import com.academy.orders.domain.account.usecase.CheckAccountIdUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;

@RequiredArgsConstructor
@Component("checkAccountIdUseCaseImpl")
public class CheckAccountIdUseCaseImpl implements CheckAccountIdUseCase {
  private final SecurityUtils securityUtils;

  @Override
  public boolean hasSameId(Long userId) {
    Long claimedId = securityUtils.getAuthenticatedUserId();
    return Objects.equals(claimedId, userId);
  }
}
