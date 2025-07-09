package com.academy.orders.infrastructure.accountV2.repository;

import com.academy.orders.domain.accountV3.entity.AccountV2;
import com.academy.orders.domain.accountV3.repository.AccountV2Repository;
import com.academy.orders.infrastructure.account.repository.AccountJpaAdapter;
import com.academy.orders.infrastructure.accountV2.AccountV2Mapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AccountV2RepositoryImpl implements AccountV2Repository {
    private final AccountJpaAdapter accountJpaAdapter;

    private final AccountV2Mapper mapper;

    @Override
    public Optional<AccountV2> findById(Long id) {
        var accountEntity = accountJpaAdapter.findById(id);
        return accountEntity.map(mapper::fromEntity);
    }
}
