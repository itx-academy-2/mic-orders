package com.academy.orders.boot.infrastructure.accountV2.repository;

import com.academy.orders.boot.infrastructure.common.repository.AbstractRepositoryIT;
import com.academy.orders.domain.accountv2.entity.AccountV2;
import com.academy.orders.domain.accountv2.repository.AccountV2Repository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Optional;

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
}
