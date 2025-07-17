package com.academy.orders.application.passwordreset;

import static com.academy.orders.application.ModelUtils.*;
import com.academy.orders.application.passwordreset.usecase.CreateTokenUseCaseImpl;
import com.academy.orders.domain.account.entity.Account;
import com.academy.orders.domain.account.factory.PasswordResetTokenFactory;
import com.academy.orders.domain.account.repository.AccountRepository;
import com.academy.orders.domain.passwordreset.entity.PasswordResetToken;
import com.academy.orders.domain.passwordreset.exception.InvalidEmailException;
import com.academy.orders.domain.passwordreset.repository.PasswordResetTokenRepository;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Qualifier;

@ExtendWith(MockitoExtension.class)
class CreateTokenUseCaseImplTest {

  @Mock
  private AccountRepository accountRepository;

  @Mock
  private PasswordResetTokenRepository tokenRepository;

  @Mock
  @Qualifier("primaryTokenFactory")
  private PasswordResetTokenFactory tokenFactory;

  @InjectMocks
  private CreateTokenUseCaseImpl createTokenUseCase;

  private static final String TEST_EMAIL = "test@example.com";

  private static final Long ACCOUNT_ID = 1L;

  private static final String TOKEN_VALUE = "token-123";

  private Account account;

  private PasswordResetToken token;

  @BeforeEach
  void setUp() {
    account = createAccount(ACCOUNT_ID, TEST_EMAIL);
    token = createPasswordResetToken(TOKEN_VALUE, TEST_EMAIL, ACCOUNT_ID);
  }

  @Test
  void createPrimaryToken_AccountExists_ReturnsTokenValue() {
    // Given
    when(accountRepository.findAccountByEmail(TEST_EMAIL)).thenReturn(Optional.of(account));
    when(tokenFactory.createToken(ACCOUNT_ID, TEST_EMAIL)).thenReturn(token);
    when(tokenRepository.save(token)).thenReturn(token);

    // When
    var result = createTokenUseCase.createPrimaryToken(TEST_EMAIL);

    // Then
    assertEquals(TOKEN_VALUE, result);
    verify(accountRepository).findAccountByEmail(TEST_EMAIL);
    verify(tokenFactory).createToken(ACCOUNT_ID, TEST_EMAIL);
    verify(tokenRepository).save(token);
  }

  @Test
  void createPrimaryToken_AccountNotFound_ReturnsRandomUUID() {
    // Given
    when(accountRepository.findAccountByEmail(TEST_EMAIL)).thenReturn(Optional.empty());

    // When
    String result = createTokenUseCase.createPrimaryToken(TEST_EMAIL);

    // Then
    assertNotNull(result);
    assertDoesNotThrow(() -> UUID.fromString(result));
    verify(accountRepository).findAccountByEmail(TEST_EMAIL);
    verifyNoInteractions(tokenFactory, tokenRepository);
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = {"   "})
  void createPrimaryToken_InvalidEmail_ThrowsIllegalArgumentException(String invalidEmail) {
    // When & Then
    var exception = assertThrows(InvalidEmailException.class,
        () -> createTokenUseCase.createPrimaryToken(invalidEmail));

    if (invalidEmail == null) {
      assertEquals("Email cannot be null", exception.getMessage());
    } else {
      assertEquals("Email cannot be empty or blank", exception.getMessage());
    }

    verifyNoInteractions(accountRepository, tokenFactory, tokenRepository);
  }

  @Test
  void createPrimaryToken_AccountRepositoryThrowsException_PropagatesException() {
    // Given
    when(accountRepository.findAccountByEmail(TEST_EMAIL)).thenThrow(new RuntimeException("Database error"));

    // When & Then
    var exception = assertThrows(
        RuntimeException.class,
        () -> createTokenUseCase.createPrimaryToken(TEST_EMAIL));
    assertEquals("Database error", exception.getMessage());
    verify(accountRepository).findAccountByEmail(TEST_EMAIL);
    verifyNoInteractions(tokenFactory, tokenRepository);
  }

  @Test
  void createPrimaryToken_TokenFactoryThrowsException_PropagatesException() {
    // Given
    when(accountRepository.findAccountByEmail(TEST_EMAIL)).thenReturn(Optional.of(account));
    when(tokenFactory.createToken(ACCOUNT_ID, TEST_EMAIL)).thenThrow(new RuntimeException("Factory error"));

    // When & Then
    var exception = assertThrows(
        RuntimeException.class,
        () -> createTokenUseCase.createPrimaryToken(TEST_EMAIL));
    assertEquals("Factory error", exception.getMessage());
    verify(accountRepository).findAccountByEmail(TEST_EMAIL);
    verify(tokenFactory).createToken(ACCOUNT_ID, TEST_EMAIL);
  }

  @Test
  void createPrimaryToken_MultipleCallsWithSameEmail_CreatesNewTokenEachTime() {
    // Given
    var token2 = createPasswordResetToken("token-456", TEST_EMAIL, ACCOUNT_ID);
    when(accountRepository.findAccountByEmail(TEST_EMAIL)).thenReturn(Optional.of(account));
    when(tokenFactory.createToken(ACCOUNT_ID, TEST_EMAIL)).thenReturn(token).thenReturn(token2);
    when(tokenRepository.save(any(PasswordResetToken.class))).thenReturn(token).thenReturn(token2);

    // When
    var result1 = createTokenUseCase.createPrimaryToken(TEST_EMAIL);
    var result2 = createTokenUseCase.createPrimaryToken(TEST_EMAIL);

    // Then
    assertEquals(TOKEN_VALUE, result1);
    assertEquals("token-456", result2);
    verify(accountRepository, times(2)).findAccountByEmail(TEST_EMAIL);
    verify(tokenFactory, times(2)).createToken(ACCOUNT_ID, TEST_EMAIL);
    verify(tokenRepository, times(2)).save(any(PasswordResetToken.class));
  }

  @Test
  void createPrimaryToken_CaseInsensitiveEmail_ReturnsTokenValue() {
    // Given
    var mixedCaseEmail = "Test@ExAmPlE.com";
    when(accountRepository.findAccountByEmail(mixedCaseEmail)).thenReturn(Optional.of(account));
    when(tokenFactory.createToken(ACCOUNT_ID, mixedCaseEmail)).thenReturn(token);
    when(tokenRepository.save(token)).thenReturn(token);

    // When
    var result = createTokenUseCase.createPrimaryToken(mixedCaseEmail);

    // Then
    assertEquals(TOKEN_VALUE, result);
    verify(accountRepository).findAccountByEmail(mixedCaseEmail);
    verify(tokenFactory).createToken(ACCOUNT_ID, mixedCaseEmail);
    verify(tokenRepository).save(token);
  }

  @Test
  void createPrimaryToken_TransactionalRollbackOnSaveFailure() {
    // Given
    when(accountRepository.findAccountByEmail(TEST_EMAIL)).thenReturn(Optional.of(account));
    when(tokenFactory.createToken(ACCOUNT_ID, TEST_EMAIL)).thenReturn(token);
    doThrow(new RuntimeException("Save error")).when(tokenRepository).save(token);

    // When & Then
    assertThrows(
        RuntimeException.class,
        () -> createTokenUseCase.createPrimaryToken(TEST_EMAIL));

    verify(accountRepository).findAccountByEmail(TEST_EMAIL);
    verify(tokenFactory).createToken(ACCOUNT_ID, TEST_EMAIL);
    verify(tokenRepository).save(token);
  }

  @Test
  void createPrimaryToken_InvalidEmailFormat_ThrowsInvalidEmailException() {
    // Given
    var invalidEmail = "invalid-email";

    // When & Then
    var exception = assertThrows(
        InvalidEmailException.class,
        () -> createTokenUseCase.createPrimaryToken(invalidEmail));
    assertEquals("Invalid email format", exception.getMessage());
    verifyNoInteractions(accountRepository, tokenFactory, tokenRepository);
  }
}
