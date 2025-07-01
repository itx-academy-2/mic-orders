package com.academy.orders.application.accountV2.usecase;

import com.academy.orders.application.ModelUtils;
import com.academy.orders.domain.account.exception.AccountNotFoundException;
import com.academy.orders.domain.accountv2.entity.AccountV2;
import com.academy.orders.domain.accountv2.repository.AccountV2Repository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetUserAccountV2InfoUseCaseImplTest {
  @InjectMocks
  private GetUserAccountV2InfoUseCaseImpl getUserAccountV2InfoUseCase;

  @Mock
  private AccountV2Repository accountV2Repository;

  @Test
  void getUserAccountInfoWithExistsAccountTest() {
    // Given
    Long existsAccountID = 1L;
    AccountV2 expected = ModelUtils.getAccountV2();
    when(accountV2Repository.findAccountById(existsAccountID)).thenReturn(Optional.of(expected));

    // When
    AccountV2 result = getUserAccountV2InfoUseCase.getUserAccountInfo(existsAccountID);

    // Then
    assertEquals(expected, result);
  }

  @Test
  void getUserAccountInfoWithNotExistsAccountTest() {
    // Given
    Long notExistsAccountID = 999L;
    when(accountV2Repository.findAccountById(notExistsAccountID)).thenThrow(AccountNotFoundException.class);

    // When / Then
    assertThrows(AccountNotFoundException.class, () -> getUserAccountV2InfoUseCase.getUserAccountInfo(notExistsAccountID));
  }
}
