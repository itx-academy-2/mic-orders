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

  private final OrderV1DuplicateAdapter orderV1DuplicateAdapter;

  @Override
  @Transactional
  public UUID save(OrderV2 orderV2, Long accountId) {
    log.info("Saving new orderV2 for accountId={}", accountId);
    var orderV2Entity = mapper.toEntity(orderV2);
    addAccountToOrder(orderV2Entity, accountId);
    var postAddressEntity = createPostAddress(orderV2.postAddress(), accountId);

    orderV2Entity.setPostAddress(postAddressEntity);
    mapOrderItemsWithProductsAndOrder(orderV2Entity);

    var createdOrderV2Entity = jpaAdapter.save(orderV2Entity);
    var savedId = createdOrderV2Entity.getId();
    log.info("OrderV2 saved successfully with id={}", savedId);

    log.info("Starting the duplication of OrderV2, PostAddressV2 and OrderItemsV2 to the V1 tables");
    orderV1DuplicateAdapter.duplicateOrderV2ToV1(createdOrderV2Entity); // Calling the temporary method to duplicate the created OrderV2
                                                                        // with OrderItems and PostAddress to the V1 of the tables
    log.info("Finished the duplication of OrderV2, PostAddressV2 and OrderItemsV2 to the V1 tables");

    return savedId;
  }

  private PostAddressV2Entity createPostAddress(PostAddressV2 postAddressV2, Long accountId) {
    if (postAddressV2.title() != null) {
      var existingPostAddressEntity = findExistingPostAddressEntityByTitleAndAccountId("permanent: " + postAddressV2.title(), accountId);
      if (existingPostAddressEntity.isPresent()) {
        log.debug("Post address title already exists for accountId={}: {}", accountId, postAddressV2.title());
        if (!checkIfPostAddressesEqual(existingPostAddressEntity.get(), postAddressV2)) {
          throw new PostAddressTitleAlreadyExistsException("This PostAddress title already exists: " + postAddressV2.title());
        } else {
          return existingPostAddressEntity.get();
        }
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

  private boolean checkIfPostAddressesEqual(PostAddressV2Entity postAddressV2Entity, PostAddressV2 postAddressV2) {
    return postAddressV2Entity.getCity().equals(postAddressV2.city())
        && postAddressV2Entity.getDeliveryMethod().equals(postAddressV2.deliveryMethod())
        && postAddressV2Entity.getDepartment().equals(postAddressV2.department())
        && postAddressV2Entity.getTitle().equals("permanent: " + postAddressV2.title())
        && postAddressV2Entity.getRecipientFirstName().equals(postAddressV2.recipientFirstName())
        && postAddressV2Entity.getRecipientLastName().equals(postAddressV2.recipientLastName())
        && postAddressV2Entity.getRecipientPhone().equals(postAddressV2.recipientPhone());
  }
}
