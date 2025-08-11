package com.academy.orders.boot.infrastructure.accountV2.repository;

import com.academy.orders.boot.infrastructure.common.repository.AbstractRepositoryIT;
import com.academy.orders.domain.accountv2.dto.UpdateUserAccountV2InfoDto;
import com.academy.orders.domain.accountv2.entity.AccountV2;
import com.academy.orders.domain.accountv2.repository.AccountV2Repository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Optional;

import static com.academy.orders.ModelUtils.getUpdateUserAccountV2InfoDto;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class AccountV2RepositoryIT extends AbstractRepositoryIT {
  @Autowired
  private AccountV2Repository accountV2Repository;

  @Test
  void findAccountByIdReturnsAccountWhenIdExistsTest() {
    // Given
    Long existingId = 1L;

    // When
    Optional<AccountV2> result = accountV2Repository.findAccountById(existingId);

    // Then
    assertThat(result).isPresent();
    assertThat(result.get().id()).isEqualTo(existingId);
  }

  @Test
  void findAccountByIdReturnEmptyWhenIdNotExistsTest() {
    // Given
    Long nonExistingId = 999L;

    // When
    Optional<AccountV2> result = accountV2Repository.findAccountById(nonExistingId);

    // Then
    assertThat(result).isNotPresent();
  }

  @Test
  void updateAccountPersonalInfoUpdatesDataTest() {
    // Given
    Long existingId = 1L;
    UpdateUserAccountV2InfoDto updateDto = getUpdateUserAccountV2InfoDto();

    // When
    accountV2Repository.updateAccountPersonalInfo(existingId, updateDto);

    // Then
    Optional<AccountV2> updatedAccount = accountV2Repository.findAccountById(existingId);
    assertThat(updatedAccount).isPresent();
    assertThat(updatedAccount.get().firstName()).isEqualTo(updateDto.firstName());
    assertThat(updatedAccount.get().lastName()).isEqualTo(updateDto.lastName());
    assertThat(updatedAccount.get().phone()).isEqualTo(updateDto.phone());
  }

  @Test
  void updateAccountPhotoUpdatesPhotoUrlTest() {
    // Given
    Long existingId = 1L;
    String newPhotoUrl = "https://example.com/new-photo.jpg";

    // When
    accountV2Repository.updateAccountPhoto(existingId, newPhotoUrl);

    // Then
    Optional<AccountV2> updatedAccount = accountV2Repository.findAccountById(existingId);
    assertThat(updatedAccount).isPresent();
    assertThat(updatedAccount.get().photo()).isEqualTo(newPhotoUrl);
  }

  @Test
  void updateAccountPhotoDoesNothingForNonExistingIdTest() {
    // Given
    Long nonExistingId = 999L;
    String newPhotoUrl = "https://example.com/new-photo.jpg";

    // When
    // Should not throw and should not update anything
    assertThatCode(() -> accountV2Repository.updateAccountPhoto(nonExistingId, newPhotoUrl))
        .doesNotThrowAnyException();

    // Then
    Optional<AccountV2> account = accountV2Repository.findAccountById(nonExistingId);
    assertThat(account).isNotPresent();
  }
}
