package com.academy.orders.boot.infrastructure.postaddress.repository;

import com.academy.orders.boot.infrastructure.common.repository.AbstractRepositoryIT;
import com.academy.orders.domain.postaddress.entity.PostAddressV2;
import com.academy.orders.domain.postaddress.repository.PostAddressV2Repository;
import com.academy.orders.infrastructure.postaddress.PostAddressV2Mapper;
import com.academy.orders.infrastructure.postaddress.entity.PostAddressV2Entity;
import com.academy.orders.infrastructure.postaddress.repository.PostAddressJpaAdapter;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.academy.orders.ModelUtils.getPostAddressV2WithPermanentAddress;
import static com.academy.orders.ModelUtils.getPostAddressV2WithPermanentAddressNewData;
import static com.academy.orders.ModelUtils.getPostAddressV2WithTemporaryAddress;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class PostAddressV2RepositoryIT extends AbstractRepositoryIT {
  @Autowired
  private PostAddressJpaAdapter postAddressJpaAdapter;

  @Autowired
  private PostAddressV2Repository postAddressV2Repository;

  @Autowired
  private PostAddressV2Mapper postAddressV2Mapper;

  @Autowired
  private EntityManager entityManager;

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

  @Test
  void checkIfAddressExistsById_ReturnsTrue_Test() {
    // Given
    PostAddressV2 postAddressV2ToSave = getPostAddressV2WithPermanentAddress();
    PostAddressV2Entity savedEntity = postAddressJpaAdapter.save(postAddressV2Mapper.toEntity(postAddressV2ToSave));

    // When
    var result = postAddressV2Repository.checkIfAddressExistsById(savedEntity.getId());

    // Then
    assertNotNull(savedEntity.getId());
    assertTrue(result);
  }

  @Test
  void checkIfAddressExistsById_ReturnsFalse_Test() {
    // Given
    var noneExistentAddressId = UUID.randomUUID();

    // When
    var result = postAddressV2Repository.checkIfAddressExistsById(noneExistentAddressId);

    // Then
    assertFalse(result);
  }

  @Test
  void checkIfAddressIsPermanent_ReturnsTrue_Test() {
    // Given
    PostAddressV2 postAddressV2ToSave = getPostAddressV2WithPermanentAddress();
    PostAddressV2Entity savedEntity = postAddressJpaAdapter.save(postAddressV2Mapper.toEntity(postAddressV2ToSave));

    // When
    var result = postAddressV2Repository.checkIfAddressIsPermanent(savedEntity.getId());

    // Then
    assertNotNull(savedEntity.getId());
    assertEquals("permanent: Friend", savedEntity.getTitle());
    assertTrue(result);
  }

  @Test
  void checkIfAddressIsPermanent_ReturnsFalse_Test() {
    // Given
    PostAddressV2 postAddressV2ToSave = getPostAddressV2WithTemporaryAddress();
    PostAddressV2Entity savedEntity = postAddressJpaAdapter.save(postAddressV2Mapper.toEntity(postAddressV2ToSave));

    // When
    var result = postAddressV2Repository.checkIfAddressIsPermanent(savedEntity.getId());

    // Then
    assertNotNull(savedEntity.getId());
    assertEquals("temp: 550e8400-e29b-41d4-a716-446655440016", savedEntity.getTitle());
    assertFalse(result);
  }

  @Test
  void deletePermanentAddress_Success_Test() {
    // Given
    PostAddressV2 postAddressV2ToSave = getPostAddressV2WithPermanentAddress();
    PostAddressV2Entity savedEntity = postAddressJpaAdapter.save(postAddressV2Mapper.toEntity(postAddressV2ToSave));

    // When
    postAddressV2Repository.deletePermanentAddress(savedEntity.getAccount().getId(), savedEntity.getId());

    // Then
    assertNotNull(savedEntity.getId());
    assertEquals("permanent: Friend", savedEntity.getTitle());
    entityManager.flush();
    entityManager.clear();
    var reloadedEntity = postAddressJpaAdapter.findById(savedEntity.getId()).orElseThrow();
    assertTrue(reloadedEntity.getTitle().startsWith("temp: "));
  }

  @Test
  void deletePermanentAddress_IsIdempotentTitleChangedOnlyOnce_Test() {
    // Given
    PostAddressV2 postAddressV2ToSave = getPostAddressV2WithPermanentAddress();
    PostAddressV2Entity savedEntity = postAddressJpaAdapter.save(postAddressV2Mapper.toEntity(postAddressV2ToSave));

    // When: first update
    postAddressV2Repository.deletePermanentAddress(savedEntity.getAccount().getId(), savedEntity.getId());

    // Then: changed title to temp
    assertNotNull(savedEntity.getId());
    assertEquals("permanent: Friend", savedEntity.getTitle());
    entityManager.flush();
    entityManager.clear();
    var reloadedEntity = postAddressJpaAdapter.findById(savedEntity.getId()).orElseThrow();
    assertTrue(reloadedEntity.getTitle().startsWith("temp: "));

    // When: attempt of second update
    postAddressV2Repository.deletePermanentAddress(savedEntity.getAccount().getId(), savedEntity.getId());

    // Then: the title wasn't changed again - the operation was idempotent
    entityManager.flush();
    entityManager.clear();
    var reloadedEntityAfterOneMoreAttempt = postAddressJpaAdapter.findById(savedEntity.getId()).orElseThrow();
    assertEquals(reloadedEntity.getTitle(), reloadedEntityAfterOneMoreAttempt.getTitle());
  }
}
