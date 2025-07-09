package com.academy.orders.infrastructure.accountV2.repository;

import com.academy.orders.domain.accountV3.entity.AccountV3;
import com.academy.orders.domain.accountV3.repository.AccountV3Repository;
import com.academy.orders.infrastructure.account.repository.AccountJpaAdapter;
import com.academy.orders.infrastructure.accountV2.AccountV3Mapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AccountV2RepositoryImpl implements AccountV3Repository {
    private final AccountJpaAdapter accountJpaAdapter;

    private final AccountV3Mapper mapper;

    @Override
    public Optional<AccountV3> findById(Long id) {
        var accountEntity = accountJpaAdapter.findById(id);
        return accountEntity.map(mapper::fromEntity);
    }
}
