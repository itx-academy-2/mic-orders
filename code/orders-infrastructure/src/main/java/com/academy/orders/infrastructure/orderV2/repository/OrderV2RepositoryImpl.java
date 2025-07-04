package com.academy.orders.infrastructure.orderV2.repository;

import com.academy.orders.domain.orderV2.entity.OrderV2;
import com.academy.orders.domain.orderV2.repository.OrderV2Repository;
import com.academy.orders.infrastructure.account.repository.AccountJpaAdapter;
import com.academy.orders.infrastructure.order.entity.OrderEntity;
import com.academy.orders.infrastructure.order.repository.OrderJpaAdapter;
import com.academy.orders.infrastructure.orderV2.OrderV2Mapper;
import com.academy.orders.infrastructure.product.repository.ProductJpaAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class OrderV2RepositoryImpl implements OrderV2Repository {
    private final OrderJpaAdapter jpaAdapter;

    private final OrderV2Mapper mapper;

    private final AccountJpaAdapter accountJpaAdapter;

    private final ProductJpaAdapter productJpaAdapter;

    @Override
    @Transactional
    public UUID save(OrderV2 order, Long accountId) {
        var orderEntity = getOrderEntityWithPostAddress(order);
        addAccountToOrder(orderEntity, accountId);
        mapOrderItemsWithProductsAndOrder(orderEntity);

        return jpaAdapter.save(orderEntity).getId();
    }

    private OrderEntity getOrderEntityWithPostAddress(OrderV2 order) {
        var orderEntity = mapper.toEntity(order);
        //orderEntity.getPostAddress().setOrder(orderEntity);
        return orderEntity;
    }

    private void addAccountToOrder(OrderEntity orderEntity, Long accountId) {
        orderEntity.setAccount(accountJpaAdapter.getReferenceById(accountId));
    }

    private void mapOrderItemsWithProductsAndOrder(OrderEntity orderEntity) {
        orderEntity.getOrderItems().forEach(item -> {
            item.setProduct(productJpaAdapter.getReferenceById(item.getProduct().getId()));
            item.setOrder(orderEntity);
        });
    }
}
