package com.academy.orders.infrastructure.orderV2.repository;

import com.academy.orders.domain.orderV2.repository.PostAddressRepository;
import com.academy.orders.infrastructure.account.repository.AccountJpaAdapter;
import com.academy.orders.infrastructure.order.entity.OrderEntity;
import com.academy.orders.infrastructure.order.entity.PostAddressEntity;
import com.academy.orders.infrastructure.orderV2.PostAddressV2Mapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class PostAddressRepositoryImpl implements PostAddressRepository {
    private final PostAddressJpaAdapter postAddressJpaAdapter;

    private final PostAddressV2Mapper mapper;

    private final AccountJpaAdapter accountJpaAdapter;

    private void addAccountToPostAddress(PostAddressEntity postAddressEntity, Long accountId) {
        //postAddressEntity.setAccount(accountJpaAdapter.getReferenceById(accountId));
    }
}
