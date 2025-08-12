package com.academy.orders.application.accountV2.usecase;

import com.academy.orders.domain.account.exception.AccountNotFoundException;
import com.academy.orders.domain.accountv2.repository.AccountV2Repository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeleteUserProfilePhotoUseCaseImplTest {
  @InjectMocks
  private DeleteUserProfilePhotoUseCaseImpl deleteUserProfilePhotoUseCase;

  @Mock
  private AccountV2Repository accountV2Repository;

  @Test
  void deleteProfilePhotoWhenAccountExistsTest() {
    // Given
    Long userId = 1L;
    when(accountV2Repository.existsById(userId)).thenReturn(true);

    // When
    deleteUserProfilePhotoUseCase.deleteProfilePhoto(userId);

    // Then
    verify(accountV2Repository, times(1)).existsById(userId);
    verify(accountV2Repository, times(1)).updateAccountPhoto(userId, null);
  }

  @Test
  void deleteProfilePhotoWhenAccountDoesNotExistTest() {
    // Given
    Long userId = 999L;
    when(accountV2Repository.existsById(userId)).thenReturn(false);

    // When
    AccountNotFoundException ex =
        assertThrows(AccountNotFoundException.class, () -> deleteUserProfilePhotoUseCase.deleteProfilePhoto(userId));

    // Then
    assertEquals(userId, ex.getAccountId());
    verify(accountV2Repository, times(1)).existsById(userId);
    verify(accountV2Repository, never()).updateAccountPhoto(anyLong(), any());
  }
}
