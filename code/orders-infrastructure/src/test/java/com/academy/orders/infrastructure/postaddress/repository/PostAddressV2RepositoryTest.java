package com.academy.orders.infrastructure.postaddress.repository;

import com.academy.orders.infrastructure.postaddress.PostAddressV2Mapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static com.academy.orders.infrastructure.ModelUtils.getPostAddressV2EntityWithPermanentTitle;
import static com.academy.orders.infrastructure.ModelUtils.getPostAddressV2WithPermanentTitle;
import static com.academy.orders.infrastructure.ModelUtils.getPostAddressV2WithCleanedPermanentTitle;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class PostAddressV2RepositoryTest {
  @Mock
  private PostAddressJpaAdapter postAddressJpaAdapter;

  @Mock
  private PostAddressV2Mapper mapper;

  @InjectMocks
  private PostAddressV2RepositoryImpl postAddressV2Repository;

  @Test
  void getPermanentPostAddressesByUserId_Success_Test() {
    // Given
    var userId = 1L;
    var postAddressEntity = getPostAddressV2EntityWithPermanentTitle();
    var mappedDomainObject = getPostAddressV2WithPermanentTitle();
    var expectedPostAddressWithCleanedTitle = getPostAddressV2WithCleanedPermanentTitle();

    when(postAddressJpaAdapter.findPermanentPostAddressesByAccountId(userId)).thenReturn(List.of(postAddressEntity));
    when(mapper.fromEntity(postAddressEntity)).thenReturn(mappedDomainObject);

    // When
    var result = postAddressV2Repository.getPermanentPostAddressesByUserId(userId);

    // Then
    assertEquals(1, result.size());
    var actual = result.get(0);

    assertEquals(expectedPostAddressWithCleanedTitle.id(), actual.id());
    assertEquals(expectedPostAddressWithCleanedTitle.title(), actual.title());
    assertEquals(expectedPostAddressWithCleanedTitle.city(), actual.city());
    assertEquals(expectedPostAddressWithCleanedTitle.deliveryMethod(), actual.deliveryMethod());
    assertEquals(expectedPostAddressWithCleanedTitle.department(), actual.department());
    assertEquals(expectedPostAddressWithCleanedTitle.account().id(), actual.account().id());
    assertEquals(expectedPostAddressWithCleanedTitle.recipientFirstName(), actual.recipientFirstName());
    assertEquals(expectedPostAddressWithCleanedTitle.recipientLastName(), actual.recipientLastName());
    assertEquals(expectedPostAddressWithCleanedTitle.recipientPhone(), actual.recipientPhone());
    assertEquals(expectedPostAddressWithCleanedTitle.orders().get(0).id(), actual.orders().get(0).id());

    verify(postAddressJpaAdapter).findPermanentPostAddressesByAccountId(userId);
    verify(mapper).fromEntity(postAddressEntity);
  }

  @Test
  void getPermanentPostAddressesByUserId_ReturnsEmptyListWhenNoAddressesFound_Test() {
    // Given
    var userId = 1L;

    when(postAddressJpaAdapter.findPermanentPostAddressesByAccountId(userId)).thenReturn(List.of());

    // When
    var result = postAddressV2Repository.getPermanentPostAddressesByUserId(userId);

    // Then
    assertEquals(0, result.size());

    verify(postAddressJpaAdapter).findPermanentPostAddressesByAccountId(userId);
    verifyNoInteractions(mapper);
  }

  @Test
  void deletePermanentAddress_Success_Test() {
    // Given
    var userId = 1L;
    var addressId = UUID.fromString("5b08cd5a-a34e-4bf1-aabf-f2e79740919b");

    doNothing().when(postAddressJpaAdapter).setAddressTitleToTemporary(userId, addressId);

    // When
    postAddressV2Repository.deletePermanentAddress(userId, addressId);

    // Then
    ArgumentCaptor<Long> userIdCaptor = ArgumentCaptor.forClass(Long.class);
    ArgumentCaptor<UUID> addressIdCaptor = ArgumentCaptor.forClass(UUID.class);

    verify(postAddressJpaAdapter, times(1))
        .setAddressTitleToTemporary(userIdCaptor.capture(), addressIdCaptor.capture());

    assertThat(userIdCaptor.getValue()).isNotNull().isEqualTo(userId);
    assertThat(addressIdCaptor.getValue()).isNotNull().isEqualTo(addressId);

    verifyNoMoreInteractions(postAddressJpaAdapter);
  }

  @Test
  void checkIfAddressExistsById_ReturnsTrueThenFalse_Test() {
    // Given
    UUID addressId = UUID.fromString("5b08cd5a-a34e-4bf1-aabf-f2e79740919b");
    when(postAddressJpaAdapter.existsById(addressId)).thenReturn(true, false);

    // When
    boolean first = postAddressV2Repository.checkIfAddressExistsById(addressId);
    boolean second = postAddressV2Repository.checkIfAddressExistsById(addressId);

    // Then
    assertThat(first).isTrue();
    assertThat(second).isFalse();

    ArgumentCaptor<UUID> captor = ArgumentCaptor.forClass(UUID.class);
    verify(postAddressJpaAdapter, times(2)).existsById(captor.capture());
    assertThat(captor.getAllValues()).containsExactly(addressId, addressId);
    verifyNoMoreInteractions(postAddressJpaAdapter);
  }

  @Test
  void checkIfAddressIsPermanent_ReturnsTrueThenFalse_Test() {
    // Given
    UUID addressId = UUID.fromString("5b08cd5a-a34e-4bf1-aabf-f2e79740919b");
    when(postAddressJpaAdapter.existsByIdAndTitleStartingWith(addressId, "permanent: ")).thenReturn(true, false);

    // When
    boolean first = postAddressV2Repository.checkIfAddressIsPermanent(addressId);
    boolean second = postAddressV2Repository.checkIfAddressIsPermanent(addressId);

    // Then
    assertThat(first).isTrue();
    assertThat(second).isFalse();

    ArgumentCaptor<UUID> idCaptor = ArgumentCaptor.forClass(UUID.class);
    ArgumentCaptor<String> prefixCaptor = ArgumentCaptor.forClass(String.class);
    verify(postAddressJpaAdapter, times(2))
        .existsByIdAndTitleStartingWith(idCaptor.capture(), prefixCaptor.capture());
    assertThat(idCaptor.getAllValues()).containsExactly(addressId, addressId);
    assertThat(prefixCaptor.getAllValues()).containsExactly("permanent: ", "permanent: ");
    verifyNoMoreInteractions(postAddressJpaAdapter);
  }
}
