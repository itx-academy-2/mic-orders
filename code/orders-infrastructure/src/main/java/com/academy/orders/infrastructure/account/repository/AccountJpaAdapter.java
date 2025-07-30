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

  @Query("SELECT a FROM AccountEntity a WHERE "
      + "(:#{#filter.status} IS NULL OR a.status = :#{#filter.status}) AND "
      + "(:#{#filter.role} IS NULL OR a.role = :#{#filter.role})")
  Page<AccountEntity> findAllByRoleAndStatus(@Nullable AccountManagementFilterDto filter, Pageable pageable);

  @Modifying(clearAutomatically = true)
  @Query("UPDATE AccountEntity a SET a.password = :password WHERE a.id = :id")
  void updatePasswordById(@Param("id") Long id, @Param("password") String password);

  @Query("UPDATE AccountEntity a SET a.firstName = :firstName, a.lastName = :lastName, a.phone = :phone WHERE a.id = :id")
  void updatePersonalInfo(@Param("id") Long id, @Param("firstName") String firstName, @Param("lastName") String lastName,
      @Param("phone") String phone);
}
