package com.academy.orders.application.accountV2.usecase;

import com.academy.orders.domain.account.exception.AccountNotFoundException;
import com.academy.orders.domain.accountv2.dto.UpdateUserAccountV2InfoDto;
import com.academy.orders.domain.accountv2.repository.AccountV2Repository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.academy.orders.application.ModelUtils.getUpdateUserAccountV2InfoDto;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateUserAccountV2InfoUseCaseImplTest {

  @InjectMocks
  private UpdateUserAccountV2InfoUseCaseImpl updateUserAccountV2InfoUseCase;

  @Mock
  private AccountV2Repository accountV2Repository;

  @Test
  void updateUserAccountInfoWithExistingUserTest() {
    // Given
    Long userId = 1L;
    UpdateUserAccountV2InfoDto updateDto = getUpdateUserAccountV2InfoDto();
    when(accountV2Repository.existsById(userId)).thenReturn(true);
    doNothing().when(accountV2Repository).updateAccountPersonalInfo(userId, updateDto);

    // When
    updateUserAccountV2InfoUseCase.updateUserAccountInfo(userId, updateDto);

    // Then
    verify(accountV2Repository, times(1)).existsById(userId);
    verify(accountV2Repository, times(1)).updateAccountPersonalInfo(userId, updateDto);
  }

  @Test
  void updateUserAccountInfoWithNonExistingUserTest() {
    // Given
    Long userId = 999L;
    UpdateUserAccountV2InfoDto updateDto = getUpdateUserAccountV2InfoDto();
    when(accountV2Repository.existsById(userId)).thenReturn(false);

    // When / Then
    assertThrows(AccountNotFoundException.class, () -> updateUserAccountV2InfoUseCase.updateUserAccountInfo(userId, updateDto));
    verify(accountV2Repository, times(1)).existsById(userId);
    verify(accountV2Repository, never()).updateAccountPersonalInfo(anyLong(), any());
  }
}
