package com.academy.orders.infrastructure.account.repository;

import com.academy.orders.domain.account.entity.Account;
import com.academy.orders.domain.account.entity.CreateAccountDTO;
import com.academy.orders.domain.account.entity.enumerated.Role;
import com.academy.orders.domain.account.entity.enumerated.UserStatus;
import com.academy.orders.infrastructure.ModelUtils;
import com.academy.orders.infrastructure.account.AccountMapper;
import com.academy.orders.infrastructure.account.AccountPageMapper;
import com.academy.orders.infrastructure.account.entity.AccountEntity;
import com.academy.orders.infrastructure.common.PageableMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;

import java.util.List;
import java.util.Optional;

import static com.academy.orders.infrastructure.ModelUtils.getAccount;
import static com.academy.orders.infrastructure.ModelUtils.getAccountEntity;
import static com.academy.orders.infrastructure.ModelUtils.getCreateAccountDTO;
import static com.academy.orders.infrastructure.TestConstants.TEST_EMAIL;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountRepositoryTest {
  @InjectMocks
  private AccountRepositoryImpl repository;

  @Mock
  private AccountJpaAdapter accountJpaAdapter;

  @Mock
  private AccountMapper accountMapper;

  @Mock
  private AccountPageMapper accountPageMapper;

  @Mock
  private PageableMapper pageableMapper;

  @Test
  void findAccountByEmailTest() {
    var accountEntity = getAccountEntity();
    var accountDomain = getAccount();

    when(accountJpaAdapter.findByEmail(accountEntity.getEmail())).thenReturn(Optional.of(accountEntity));
    when(accountMapper.fromEntity(accountEntity)).thenReturn(accountDomain);

    var actualAccount = repository.findAccountByEmail(accountEntity.getEmail());

    assertEquals(accountDomain, actualAccount.get());

    verify(accountJpaAdapter).findByEmail(accountEntity.getEmail());
    verify(accountMapper).fromEntity(accountEntity);
  }

  @Test
  void findAccountByEmailIfAccountAbsentTest() {
    var mail = "test@mail.com";

    when(accountJpaAdapter.findByEmail(mail)).thenReturn(Optional.empty());
    var actualAccount = repository.findAccountByEmail(mail);

    assertTrue(actualAccount.isEmpty());
    verify(accountJpaAdapter).findByEmail(mail);
    verify(accountMapper, never()).fromEntity(any(AccountEntity.class));
  }

  @ParameterizedTest
  @CsvSource({"true", "false"})
  void existsByEmailTest(boolean exists) {
    var mail = "test@mail.com";

    when(this.accountJpaAdapter.existsByEmail(mail)).thenReturn(exists);
    var result = this.repository.existsByEmail(mail);
    assertEquals(exists, result);
    verify(accountJpaAdapter).existsByEmail(mail);
  }

  @ParameterizedTest
  @CsvSource({"true", "false"})
  void existsByIdTest(boolean exists) {
    var id = 1L;
    when(this.accountJpaAdapter.existsById(id)).thenReturn(exists);
    var result = this.repository.existsById(id);
    assertEquals(exists, result);
    verify(accountJpaAdapter).existsById(id);
  }

  @Test
  void saveTest() {
    AccountEntity mappedAccountEntity = getAccountEntity();
    mappedAccountEntity.setRole(null);
    mappedAccountEntity.setStatus(null);
    AccountEntity preSavedAccountEntity = getAccountEntity();
    preSavedAccountEntity.setRole(Role.ROLE_USER);
    preSavedAccountEntity.setStatus(UserStatus.ACTIVE);
    AccountEntity savedAccountEntity = getAccountEntity();
    CreateAccountDTO createAccountDTO = getCreateAccountDTO();
    Account accountDomain = getAccount();

    when(accountJpaAdapter.save(preSavedAccountEntity)).thenReturn(savedAccountEntity);
    when(accountMapper.toEntity(createAccountDTO)).thenReturn(mappedAccountEntity);
    when(accountMapper.fromEntity(savedAccountEntity)).thenReturn(accountDomain);

    Account actualAccount = repository.save(createAccountDTO);

    assertEquals(accountDomain, actualAccount);
    verify(accountJpaAdapter).save(preSavedAccountEntity);
    verify(accountMapper).toEntity(createAccountDTO);
    verify(accountMapper).fromEntity(savedAccountEntity);
  }

  @Test
  void findRoleByEmailTest() {
    var expectedRole = Role.ROLE_MANAGER;
    when(accountJpaAdapter.findRoleByEmail(TEST_EMAIL)).thenReturn(Optional.of(expectedRole));
    var result = repository.findRoleByEmail(TEST_EMAIL);
    assertEquals(expectedRole, result.get());
    verify(accountJpaAdapter).findRoleByEmail(TEST_EMAIL);
  }

  @Test
  void updateStatusTest() {
    var id = 1L;
    var status = UserStatus.ACTIVE;

    doNothing().when(this.accountJpaAdapter).updateStatus(id, UserStatus.ACTIVE);
    assertDoesNotThrow(() -> this.repository.updateStatus(id, status));
    verify(accountJpaAdapter).updateStatus(id, status);
  }

  @Test
  void getAccountsTest() {
    var filter = ModelUtils.getAccountManagementFilterDto();
    var pageableDomain = ModelUtils.getPageable();

    var pageable = ModelUtils.getPageRequest();
    var accountEntities = List.of(getAccountEntity());
    var accountEntityPage = new PageImpl<>(accountEntities, pageable, accountEntities.size());
    var accountDomains = List.of(getAccount());
    var expectedPage = ModelUtils.getAccountPage(accountDomains, pageableDomain, accountEntities.size(), 1);

    when(pageableMapper.fromDomain(pageableDomain)).thenReturn(pageable);
    when(accountJpaAdapter.findAllByRoleAndStatus(filter, pageable)).thenReturn(accountEntityPage);
    when(accountPageMapper.toDomain(accountEntityPage)).thenReturn(expectedPage);

    var actualPage = repository.getAccounts(filter, pageableDomain);

    assertEquals(expectedPage, actualPage);

    verify(pageableMapper).fromDomain(pageableDomain);
    verify(accountJpaAdapter).findAllByRoleAndStatus(filter, pageable);
    verify(accountPageMapper).toDomain(accountEntityPage);
  }
}
