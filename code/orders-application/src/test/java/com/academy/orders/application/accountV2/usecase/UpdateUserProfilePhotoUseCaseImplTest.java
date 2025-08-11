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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateUserProfilePhotoUseCaseImplTest {
  @InjectMocks
  private UpdateUserProfilePhotoUseCaseImpl updateUserProfilePhotoUseCase;

  @Mock
  private AccountV2Repository accountV2Repository;

  @Test
  void updateProfilePhotoWhenAccountExists() {
    // Given
    Long userId = 1L;
    String photoUrl = "http://example.com/new-photo.jpg";
    when(accountV2Repository.existsById(userId)).thenReturn(true);

    // When
    updateUserProfilePhotoUseCase.updateProfilePhoto(userId, photoUrl);

    // Then
    verify(accountV2Repository, times(1)).existsById(userId);
    verify(accountV2Repository, times(1)).updateAccountPhoto(userId, photoUrl);
  }

  @Test
  void updateProfilePhotoWhenAccountDoesNotExist() {
    // Given
    Long userId = 2L;
    String photoUrl = "http://example.com/new-photo.jpg";
    when(accountV2Repository.existsById(userId)).thenReturn(false);

    // When
    AccountNotFoundException ex =
        assertThrows(AccountNotFoundException.class, () -> updateUserProfilePhotoUseCase.updateProfilePhoto(userId, photoUrl));

    // Then
    assertEquals(userId, ex.getAccountId());
    verify(accountV2Repository, times(1)).existsById(userId);
    verify(accountV2Repository, never()).updateAccountPhoto(anyLong(), anyString());
  }
}
