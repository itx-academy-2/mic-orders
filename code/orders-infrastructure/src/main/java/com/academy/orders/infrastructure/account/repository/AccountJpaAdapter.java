package com.academy.orders.infrastructure.account.repository;

import com.academy.orders.domain.account.dto.AccountManagementFilterDto;
import com.academy.orders.domain.account.entity.enumerated.Role;
import com.academy.orders.domain.account.entity.enumerated.UserStatus;
import com.academy.orders.infrastructure.account.entity.AccountEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface AccountJpaAdapter extends JpaRepository<AccountEntity, Long> {
  Optional<AccountEntity> findByEmail(String email);

  Boolean existsByEmail(String email);

  @Query("SELECT a.role FROM AccountEntity a WHERE a.email = :email")
  Optional<Role> findRoleByEmail(String email);

  @Modifying
  @Query("UPDATE AccountEntity a SET a.status = :status WHERE a.id = :id")
  void updateStatus(Long id, UserStatus status);

  /**
   * Retrieves a paginated list of account entities filtered by role and status.
   *
   * If the filter's status or role is null, the corresponding filter is not applied.
   *
   * @param filter   optional filter containing status and role criteria
   * @param pageable pagination and sorting information
   * @return a page of account entities matching the filter criteria
   */
  @Query("SELECT a FROM AccountEntity a WHERE "
      + "(:#{#filter.status} IS NULL OR a.status = :#{#filter.status}) AND "
      + "(:#{#filter.role} IS NULL OR a.role = :#{#filter.role})")
  Page<AccountEntity> findAllByRoleAndStatus(@Nullable AccountManagementFilterDto filter, Pageable pageable);

  /**
   * Updates the password of the account identified by the given ID.
   *
   * @param id the unique identifier of the account
   * @param password the new password to set for the account
   */
  @Modifying
  @Query("UPDATE AccountEntity a SET a.password = :password WHERE a.id = :id")
  void updatePasswordById(@Param("id") Long id, @Param("password") String password);

  /**
       * Updates the first name, last name, and phone number of an account identified by its ID.
       *
       * @param id the unique identifier of the account to update
       * @param firstName the new first name to set
       * @param lastName the new last name to set
       * @param phone the new phone number to set
       */
      @Query("UPDATE AccountEntity a SET a.firstName = :firstName, a.lastName = :lastName, a.phone = :phone WHERE a.id = :id")
  void updatePersonalInfo(@Param("id") Long id, @Param("firstName") String firstName, @Param("lastName") String lastName,
      @Param("phone") String phone);
}
