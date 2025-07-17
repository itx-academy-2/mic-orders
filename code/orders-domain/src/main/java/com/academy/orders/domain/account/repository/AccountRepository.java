package com.academy.orders.domain.account.repository;

import com.academy.orders.domain.account.dto.AccountManagementFilterDto;
import com.academy.orders.domain.account.entity.Account;
import com.academy.orders.domain.account.entity.CreateAccountDTO;
import com.academy.orders.domain.account.entity.enumerated.Role;
import com.academy.orders.domain.account.entity.enumerated.UserStatus;
import com.academy.orders.domain.common.Page;
import com.academy.orders.domain.common.Pageable;

import java.util.Optional;

/**
 * Repository interface for managing accounts.
 */
public interface AccountRepository {
  /**
   * Retrieves an {@link Account} entity by its email address.
   *
   * @param email the email address of the account to be retrieved.
   * @return an {@link Optional} containing the {@link Account} entity if found, otherwise empty.
   */
  Optional<Account> findAccountByEmail(String email);

  /**
   * Checks if an account with the given email address exists.
   *
   * @param email the email address to check for existence.
   * @return {@code true} if an account with the given email exists, {@code false} otherwise.
   */
  Boolean existsByEmail(String email);

  /**
   * Saves a new account.
   *
   * @param account the {@link CreateAccountDTO} containing the account information to be saved.
   * @return the saved {@link Account} entity.
   */
  Account save(CreateAccountDTO account);

  /**
   * Retrieves a {@link Role} entity associated with an account by its email.
   *
   * @param email the email address of the account whose role is to be retrieved.
   * @return an {@link Optional} containing the {@link Role} entity if found, otherwise empty.
   * @author Anton Bondar
   */
  Optional<Role> findRoleByEmail(String email);

  /**
   * Checks if an account exists by its email.
   *
   * @param id the id of account to check for existence.
   * @return {@code true} if an account with the given id exists, otherwise {@code false}.
   * @author Denys Ryhal
   */
  Boolean existsById(Long id);

  /**
   * Updates status of an account.
   *
   * @param id the id of account to find it.
   * @param status new status of account
   * @author Denys Ryhal
   */
  void updateStatus(Long id, UserStatus status);

  /**
 * Returns a paginated list of accounts matching the specified filter criteria.
 *
 * @param filter filtering criteria for selecting accounts
 * @param pageable pagination details including page number and size
 * @return a page of accounts that satisfy the filter conditions
 */
  Page<Account> getAccounts(AccountManagementFilterDto filter, Pageable pageable);

  /**
 * Updates the password of the account with the specified ID.
 *
 * @param accountId    the unique identifier of the account whose password will be updated
 * @param newPassword  the new password to set for the account
 */
void updatePassword(Long accountId, String newPassword);
}
