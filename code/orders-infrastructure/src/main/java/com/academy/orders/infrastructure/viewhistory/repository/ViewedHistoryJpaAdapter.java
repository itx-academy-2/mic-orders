package com.academy.orders.infrastructure.viewhistory.repository;

import com.academy.orders.infrastructure.viewhistory.entity.ViewedHistoryEntity;
import com.academy.orders.infrastructure.viewhistory.entity.ViewedHistoryId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ViewedHistoryJpaAdapter extends JpaRepository<ViewedHistoryEntity, ViewedHistoryId> {

  /**
   * Retrieves a paginated list of viewed product history records for the specified account ID. The sorting behavior is determined by the
   * provided {@link Pageable} argument.
   *
   * @param accountId the ID of the account whose viewed history should be retrieved.
   * @param pageable the pagination and sorting information.
   * @return a {@link Page} containing the viewed product history records for the specified account.
   */
  Page<ViewedHistoryEntity> findAllByIdAccountId(Long accountId, Pageable pageable);

  /**
   * Deletes all viewed product history records associated with the given account ID. This method removes all entries from the
   * {@code viewed_history} table for the specified account.
   *
   * @param accountId the ID of the account whose viewed history should be deleted.
   */
  @Transactional
  @Modifying
  @Query("DELETE FROM ViewedHistoryEntity v WHERE v.id.accountId = :accountId")
  void deleteAllByAccountId(@Param("accountId") Long accountId);

}
