package com.academy.orders.infrastructure.orderV2.repository;

import com.academy.orders.domain.orderV2.entity.OrderV2;
import com.academy.orders.domain.orderV2.repository.OrderV2Repository;
import com.academy.orders.domain.postaddress.entity.PostAddressV2;
import com.academy.orders.domain.postaddress.exception.PostAddressTitleAlreadyExistsException;
import com.academy.orders.infrastructure.account.repository.AccountJpaAdapter;
import com.academy.orders.infrastructure.orderV2.OrderV2Mapper;
import com.academy.orders.infrastructure.orderV2.entity.OrderV2Entity;
import com.academy.orders.infrastructure.postaddress.PostAddressV2Mapper;
import com.academy.orders.infrastructure.postaddress.entity.PostAddressV2Entity;
import com.academy.orders.infrastructure.postaddress.repository.PostAddressJpaAdapter;
import com.academy.orders.infrastructure.product.repository.ProductJpaAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class OrderV2RepositoryImpl implements OrderV2Repository {
    private final OrderV2JpaAdapter jpaAdapter;

    private final OrderV2Mapper mapper;

    private final AccountJpaAdapter accountJpaAdapter;

    private final ProductJpaAdapter productJpaAdapter;

    private final PostAddressJpaAdapter postAddressJpaAdapter;

    private final PostAddressV2Mapper postAddressMapper;

    @Override
    @Transactional
    public UUID save(OrderV2 orderV2, Long accountId) {
        log.info("Saving new orderV2 for accountId={}", accountId);
        var orderV2Entity = mapper.toEntity(orderV2);
        addAccountToOrder(orderV2Entity, accountId);
        var postAddressEntity = createPostAddress(orderV2.postAddress(), accountId);

        orderV2Entity.setPostAddress(postAddressEntity);
        mapOrderItemsWithProductsAndOrder(orderV2Entity);

        var savedId = jpaAdapter.save(orderV2Entity).getId();
        log.info("OrderV2 saved successfully with id={}", savedId);
        return savedId;
    }

    private PostAddressV2Entity createPostAddress(PostAddressV2 postAddressV2, Long accountId) {
        if (postAddressV2.id() != null) {
            var existingPostAddressEntity = postAddressJpaAdapter.findById(postAddressV2.id());
            if (existingPostAddressEntity.isPresent()) {
                log.debug("Found existing post address by id: {}", postAddressV2.id());
                return existingPostAddressEntity.get();
            }
        }
        if (postAddressV2.title() != null) {
            var existingPostAddressEntity = findExistingPostAddressEntityByTitleAndAccountId("permanent: " + postAddressV2.title(), accountId);
            if (existingPostAddressEntity.isPresent()) {
                log.warn("Post address title already exists for accountId={}: {}", accountId, postAddressV2.title());
                throw new PostAddressTitleAlreadyExistsException("This PostAddress title already exists: " + postAddressV2.title());
            }
        }
        log.info("Creating new post address for accountId={}", accountId);
        PostAddressV2Entity postAddressV2Entity = postAddressMapper.toEntity(postAddressV2);
        addAccountToPostAddress(postAddressV2Entity, accountId);
        addPostAddressTitle(postAddressV2, postAddressV2Entity);
        return postAddressJpaAdapter.save(postAddressV2Entity);
    }

    private void addAccountToPostAddress(PostAddressV2Entity postAddressV2Entity, Long accountId) {
        postAddressV2Entity.setAccount(accountJpaAdapter.getReferenceById(accountId));
        log.debug("Linked accountId={} to post address", accountId);
    }

    private void addAccountToOrder(OrderV2Entity orderV2Entity, Long accountId) {
        orderV2Entity.setAccount(accountJpaAdapter.getReferenceById(accountId));
        log.debug("Linked accountId={} to order", accountId);
    }

    private void mapOrderItemsWithProductsAndOrder(OrderV2Entity orderV2Entity) {
        orderV2Entity.getOrderItems().forEach(item -> {
            item.setProduct(productJpaAdapter.getReferenceById(item.getProduct().getId()));
            item.setOrder(orderV2Entity);
        });
    }

    private void addPostAddressTitle(PostAddressV2 postAddressV2, PostAddressV2Entity postAddressV2Entity) {
        if (postAddressV2.title() == null) {
            postAddressV2Entity.setTitle("temp: " + UUID.randomUUID());
        } else {
            postAddressV2Entity.setTitle("permanent: " + postAddressV2.title());
        }
        log.debug("Assigned post address title: {}", postAddressV2Entity.getTitle());
    }

    private Optional<PostAddressV2Entity> findExistingPostAddressEntityByTitleAndAccountId(String title, Long accountId) {
        return postAddressJpaAdapter.findByTitleAndAccount_Id(title, accountId);
    }
}
