package com.academy.orders.infrastructure.postaddress.repository;

import com.academy.orders.domain.postaddress.entity.PostAddressV2;
import com.academy.orders.domain.postaddress.repository.PostAddressV2Repository;
import com.academy.orders.infrastructure.postaddress.PostAddressV2Mapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostAddressV2RepositoryImpl implements PostAddressV2Repository {
  private final PostAddressJpaAdapter postAddressJpaAdapter;

  private final PostAddressV2Mapper mapper;

  @Override
  public List<PostAddressV2> getPermanentPostAddressesByUserId(Long userId) {
    return postAddressJpaAdapter.findPermanentPostAddressesByAccountId(userId).stream()
        .map(mapper::fromEntity)
        .map(postAddressV2 -> PostAddressV2.builder().title(postAddressV2.title().replaceFirst("^permanent:\\s*", ""))
            .id(postAddressV2.id())
            .city(postAddressV2.city())
            .deliveryMethod(postAddressV2.deliveryMethod())
            .department(postAddressV2.department())
            .account(postAddressV2.account())
            .recipientFirstName(postAddressV2.recipientFirstName())
            .recipientLastName(postAddressV2.recipientLastName())
            .recipientPhone(postAddressV2.recipientPhone())
            .orders(postAddressV2.orders())
            .build())
        .toList();
  }

  @Override
  public void deletePermanentAddress(UUID addressId) {
    postAddressJpaAdapter.setAddressTitleToTemporary(addressId);
  }

  @Override
  public boolean checkIfAddressExistsById(UUID addressId) {
    return postAddressJpaAdapter.existsById(addressId);
  }

  @Override
  public boolean checkIfAddressIsPermanent(UUID addressId) {
    return postAddressJpaAdapter.existsByIdAndTitleStartingWith(addressId, "permanent: ");
  }
}
