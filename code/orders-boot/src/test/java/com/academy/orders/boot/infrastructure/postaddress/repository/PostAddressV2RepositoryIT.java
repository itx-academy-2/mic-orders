package com.academy.orders.boot.infrastructure.postaddress.repository;

import com.academy.orders.boot.infrastructure.common.repository.AbstractRepositoryIT;
import com.academy.orders.domain.postaddress.entity.PostAddressV2;
import com.academy.orders.domain.postaddress.repository.PostAddressV2Repository;
import com.academy.orders.infrastructure.postaddress.PostAddressV2Mapper;
import com.academy.orders.infrastructure.postaddress.entity.PostAddressV2Entity;
import com.academy.orders.infrastructure.postaddress.repository.PostAddressJpaAdapter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.academy.orders.ModelUtils.getPostAddressV2WithPermanentAddress;
import static com.academy.orders.ModelUtils.getPostAddressV2WithPermanentAddressNewData;
import static com.academy.orders.ModelUtils.getPostAddressV2WithTemporaryAddress;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PostAddressV2RepositoryIT extends AbstractRepositoryIT {
  @Autowired
  private PostAddressJpaAdapter postAddressJpaAdapter;

  @Autowired
  private PostAddressV2Repository postAddressV2Repository;

  @Autowired
  private PostAddressV2Mapper postAddressV2Mapper;

  private final Long accountId = 2L;

  @Test
  void getPermanentPostAddressesByUserId_PermanentAddressesFound_Test() {
    // Given
    PostAddressV2 postAddressV2ToSaveFirst = getPostAddressV2WithPermanentAddress();
    PostAddressV2 postAddressV2ToSaveSecond = getPostAddressV2WithPermanentAddressNewData();
    PostAddressV2 postAddressV2ToSaveThird = getPostAddressV2WithTemporaryAddress();

    PostAddressV2Entity savedFirstEntity = postAddressJpaAdapter.save(postAddressV2Mapper.toEntity(postAddressV2ToSaveFirst));
    assertNotNull(savedFirstEntity.getId());
    assertEquals("permanent: Friend", savedFirstEntity.getTitle());

    PostAddressV2Entity savedSecondEntity = postAddressJpaAdapter.save(postAddressV2Mapper.toEntity(postAddressV2ToSaveSecond));
    assertNotNull(savedSecondEntity.getId());
    assertEquals("permanent: Family", savedSecondEntity.getTitle());

    PostAddressV2Entity savedThirdEntity = postAddressJpaAdapter.save(postAddressV2Mapper.toEntity(postAddressV2ToSaveThird));
    assertNotNull(savedThirdEntity.getId());
    assertEquals("temp: 550e8400-e29b-41d4-a716-446655440016", savedThirdEntity.getTitle());

    // When
    List<PostAddressV2> listOfPostAddresses = postAddressV2Repository.getPermanentPostAddressesByUserId(accountId);

    // Then
    assertNotNull(listOfPostAddresses);

    // Check that we get only 2 addresses because the third one is temporary
    assertEquals(2, listOfPostAddresses.size());

    Set<String> titles = listOfPostAddresses.stream()
        .map(PostAddressV2::title)
        .collect(Collectors.toSet());

    // Check that the title was cleaned in the repository method
    assertTrue(titles.contains("Friend"));
    assertTrue(titles.contains("Family"));
  }

  @Test
  void getPermanentPostAddressesByUserId_PermanentAddressesNotFound_Test() {
    // When
    List<PostAddressV2> listOfPostAddresses = postAddressV2Repository.getPermanentPostAddressesByUserId(accountId);

    // Then
    assertNotNull(listOfPostAddresses);
    assertEquals(0, listOfPostAddresses.size());
  }
}
