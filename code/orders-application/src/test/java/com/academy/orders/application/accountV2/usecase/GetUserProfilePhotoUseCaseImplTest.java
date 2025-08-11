package com.academy.orders.application.accountV2.usecase;

import com.academy.orders.domain.account.exception.AccountNotFoundException;
import com.academy.orders.domain.accountv2.entity.AccountV2;
import com.academy.orders.domain.accountv2.repository.AccountV2Repository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;

import static com.academy.orders.application.ModelUtils.getAccountV2;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetUserProfilePhotoUseCaseImplTest {
  @InjectMocks
  private GetUserProfilePhotoUseCaseImpl getUserProfilePhotoUseCase;

  @Mock
  private AccountV2Repository accountV2Repository;

  @Test
  void getProfilePhotoWhenAccountExistsWithPhotoTest() {
    // Given
    Long userId = 1L;
    AccountV2 account = getAccountV2();
    when(accountV2Repository.findAccountById(userId)).thenReturn(Optional.of(account));

    // When
    String result = getUserProfilePhotoUseCase.getProfilePhoto(userId);

    // Then
    assertNotNull(result);
    assertEquals(account.photo(), result);
    verify(accountV2Repository, times(1)).findAccountById(userId);
  }

  @Test
  void getProfilePhotoWhenAccountExistsWithoutPhotoTest() {
    // Given
    Long userId = 2L;
    AccountV2 account = new AccountV2(userId, null, null, null, null, null, null, null, null, null);
    when(accountV2Repository.findAccountById(userId)).thenReturn(Optional.of(account));

    // When
    String result = getUserProfilePhotoUseCase.getProfilePhoto(userId);

    // Then
    assertNull(result);
    verify(accountV2Repository, times(1)).findAccountById(userId);
  }

  @Test
  void getProfilePhotoWhenAccountDoesNotExistTest() {
    // Given
    Long userId = 999L;
    when(accountV2Repository.findAccountById(userId)).thenReturn(Optional.empty());

    // When
    AccountNotFoundException ex = assertThrows(AccountNotFoundException.class, () -> getUserProfilePhotoUseCase.getProfilePhoto(userId));

    // Then
    assertEquals(userId, ex.getAccountId());
    verify(accountV2Repository, times(1)).findAccountById(userId);
  }
}
