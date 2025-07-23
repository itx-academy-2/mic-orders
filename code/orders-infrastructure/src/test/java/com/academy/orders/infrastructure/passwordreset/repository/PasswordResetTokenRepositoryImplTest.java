package com.academy.orders.infrastructure.passwordreset.repository;

import com.academy.orders.domain.passwordreset.entity.PasswordResetToken;
import com.academy.orders.domain.passwordreset.entity.enumerated.TokenStatus;
import com.academy.orders.domain.passwordreset.entity.enumerated.TokenType;
import static com.academy.orders.infrastructure.ModelUtils.createSampleEntity;
import static com.academy.orders.infrastructure.ModelUtils.createSampleToken;
import com.academy.orders.infrastructure.passwordreset.PasswordResetTokenMapper;
import com.academy.orders.infrastructure.passwordreset.entity.PasswordResetTokenEntity;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mapstruct.factory.Mappers.getMapper;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

class PasswordResetTokenRepositoryImplTest {
  @Mock
  private PasswordResetTokenJpaAdaptor jpaAdaptor;

  @InjectMocks
  private PasswordResetTokenRepositoryImpl repository;

  private final PasswordResetTokenMapper mapper = getMapper(PasswordResetTokenMapper.class);

  private AutoCloseable mocks;

  @BeforeEach
  void setUp() {
    mocks = MockitoAnnotations.openMocks(this);
    repository = new PasswordResetTokenRepositoryImpl(jpaAdaptor, mapper);
  }

  @AfterEach
  void tearDown() throws Exception {
    if (mocks != null) {
      mocks.close();
    }
  }

  @Test
  void save_shouldSaveAndReturnDomainObject() {
    // Given
    var domainToken = createSampleToken();
    var savedEntity = mapper.toEntity(domainToken);
    savedEntity.setId(42L);

    when(jpaAdaptor.save(any(PasswordResetTokenEntity.class))).thenReturn(savedEntity);

    // When
    var savedDomain = repository.save(domainToken);

    // Then
    assertThat(savedDomain).isNotNull();
    assertThat(savedDomain.getId()).isEqualTo(42L);
    assertThat(savedDomain.getToken()).isEqualTo(domainToken.getToken());

    verify(jpaAdaptor).save(any(PasswordResetTokenEntity.class));
  }

  @Test
  void findByToken_shouldReturnDomain_whenEntityFound() {
    // Given
    var token = "roundtrip";
    var entity = createSampleEntity();

    when(jpaAdaptor.findByTokenAndStatusAndExpiresAtAfter(eq(token), eq(TokenStatus.ACTIVE), any()))
        .thenReturn(Optional.of(entity));

    // When
    Optional<PasswordResetToken> result = repository.findByToken(token);

    // Then
    assertThat(result).isPresent();
    assertThat(result.get().getToken()).isEqualTo(token);
    assertThat(result.get().getId()).isEqualTo(5L);
    assertThat(result.get().getAccountId()).isEqualTo(100L);
    verify(jpaAdaptor).findByTokenAndStatusAndExpiresAtAfter(eq(token), eq(TokenStatus.ACTIVE), any());
  }

  @Test
  void findLatestPrimaryTokenByAccountId_shouldReturnDomain_whenEntityFound() {
    // Given
    var accountId = 100L;
    var entity = createSampleEntity();

    when(jpaAdaptor.findFirstByAccountIdAndTypeAndStatusOrderByCreatedAtDesc(accountId, TokenType.PRIMARY, TokenStatus.ACTIVE))
        .thenReturn(Optional.of(entity));

    // When
    Optional<PasswordResetToken> result = repository.findLatestPrimaryTokenByAccountId(accountId);

    // Then
    assertThat(result).isPresent();
    assertThat(result.get().getAccountId()).isEqualTo(accountId);
    assertThat(result.get().getId()).isEqualTo(5L);
    assertThat(result.get().getToken()).isEqualTo("roundtrip");
    verify(jpaAdaptor).findFirstByAccountIdAndTypeAndStatusOrderByCreatedAtDesc(accountId, TokenType.PRIMARY, TokenStatus.ACTIVE);
  }

  @Test
  void findByAccountIdAndTypeAndStatus_shouldReturnMappedList() {
    // Given
    var accountId = 100L;
    var tokenType = TokenType.PRIMARY;
    var tokenStatus = TokenStatus.ACTIVE;
    var entity1 = createSampleEntity();
    var entity2 = createSampleEntity();

    when(jpaAdaptor.findByAccountIdAndTypeAndStatus(accountId, tokenType, tokenStatus))
        .thenReturn(List.of(entity1, entity2));

    // When
    List<PasswordResetToken> tokens = repository.findByAccountIdAndTypeAndStatus(accountId, tokenType, tokenStatus);

    // Then
    assertThat(tokens).hasSize(2);
    assertThat(tokens).extracting("id").containsExactlyInAnyOrder(5L, 5L); // оба объекта одинаковы, по createSampleEntity()
    assertThat(tokens).extracting("accountId").allMatch(id -> id.equals(accountId));
    verify(jpaAdaptor).findByAccountIdAndTypeAndStatus(accountId, tokenType, tokenStatus);
  }

  @Test
  void deleteAllByStatus_shouldCallJpaAdaptor() {
    // Given
    var status = TokenStatus.USED;

    // When
    repository.deleteAllByStatus(status);

    // Then
    verify(jpaAdaptor).deleteAllByStatus(status);
  }

  @Test
  void findByToken_shouldReturnEmpty_whenEntityNotFound() {
    // Given
    var token = "nonexistent-token";
    when(jpaAdaptor.findByTokenAndStatusAndExpiresAtAfter(eq(token), eq(TokenStatus.ACTIVE), any()))
        .thenReturn(Optional.empty());

    // When
    Optional<PasswordResetToken> result = repository.findByToken(token);

    // Then
    assertThat(result).isEmpty();
    verify(jpaAdaptor).findByTokenAndStatusAndExpiresAtAfter(eq(token), eq(TokenStatus.ACTIVE), any());
  }

  @Test
  void save_shouldSetId_whenPersistingNewToken() {
    // Given
    var domainToken = createSampleToken().toBuilder().id(null).build();
    var savedEntity = mapper.toEntity(domainToken);
    savedEntity.setId(88L);

    when(jpaAdaptor.save(any())).thenReturn(savedEntity);

    // When
    var result = repository.save(domainToken);

    // Then
    assertThat(result.getId()).isEqualTo(88L);
    verify(jpaAdaptor).save(any());
  }

  @Test
  void findByAccountIdAndTypeAndStatus_shouldReturnEmptyList_whenNoTokensFound() {
    // Given
    var accountId = 123L;
    when(jpaAdaptor.findByAccountIdAndTypeAndStatus(accountId, TokenType.SECONDARY, TokenStatus.EXPIRED))
        .thenReturn(List.of());

    // When
    List<PasswordResetToken> tokens = repository.findByAccountIdAndTypeAndStatus(accountId, TokenType.SECONDARY, TokenStatus.EXPIRED);

    // Then
    assertThat(tokens).isEmpty();
    verify(jpaAdaptor).findByAccountIdAndTypeAndStatus(accountId, TokenType.SECONDARY, TokenStatus.EXPIRED);
  }

  @Test
  void findLatestPrimaryTokenByAccountId_shouldReturnEmpty_whenNotFound() {
    // Given
    var accountId = 999L;
    when(jpaAdaptor.findFirstByAccountIdAndTypeAndStatusOrderByCreatedAtDesc(accountId, TokenType.PRIMARY, TokenStatus.ACTIVE))
        .thenReturn(Optional.empty());

    // When
    Optional<PasswordResetToken> result = repository.findLatestPrimaryTokenByAccountId(accountId);

    // Then
    assertThat(result).isEmpty();
    verify(jpaAdaptor).findFirstByAccountIdAndTypeAndStatusOrderByCreatedAtDesc(accountId, TokenType.PRIMARY, TokenStatus.ACTIVE);
  }
}
