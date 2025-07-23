package com.academy.orders.infrastructure.orderV2.repository;

import com.academy.orders.domain.postaddress.exception.PostAddressTitleAlreadyExistsException;
import com.academy.orders.infrastructure.account.repository.AccountJpaAdapter;
import com.academy.orders.infrastructure.orderV2.OrderV2Mapper;
import com.academy.orders.infrastructure.orderV2.entity.OrderV2Entity;
import com.academy.orders.infrastructure.postaddress.PostAddressV2Mapper;
import com.academy.orders.infrastructure.postaddress.entity.PostAddressV2Entity;
import com.academy.orders.infrastructure.postaddress.repository.PostAddressJpaAdapter;
import com.academy.orders.infrastructure.product.repository.ProductJpaAdapter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.academy.orders.infrastructure.ModelUtils.getOrderV2Entity;
import static com.academy.orders.infrastructure.ModelUtils.getAccountEntity;
import static com.academy.orders.infrastructure.ModelUtils.getPostAddressV2Entity;
import static com.academy.orders.infrastructure.ModelUtils.getPostAddressV2EntityWithNewTitle;
import static com.academy.orders.infrastructure.ModelUtils.getProductEntity;
import static com.academy.orders.infrastructure.ModelUtils.getOrderV2EntityWithId;
import static com.academy.orders.infrastructure.ModelUtils.getOrderV2WithoutId;
import static com.academy.orders.infrastructure.ModelUtils.getPostAddressV2WithNewRecipientInfo;
import static com.academy.orders.infrastructure.ModelUtils.getOrderV2WithoutIdWithNewRecipientInfo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
public class OrderV2RepositoryTest {
    @Mock
    private OrderV2JpaAdapter jpaAdapter;

    @Mock
    private OrderV2Mapper mapper;

    @Mock
    private AccountJpaAdapter accountJpaAdapter;

    @Mock
    private ProductJpaAdapter productJpaAdapter;

    @Mock
    private PostAddressJpaAdapter postAddressJpaAdapter;

    @Mock
    private PostAddressV2Mapper postAddressMapper;

    @Mock
    private OrderV1DuplicateAdapter orderV1DuplicateAdapter;

    @InjectMocks
    private OrderV2RepositoryImpl repository;

    @Test
    void save_NoExistingPostAddressesWithGivenTitleFound_Test() {
        //Given
        var orderV2 = getOrderV2WithoutId();
        var accountId = 1L;
        var orderV2Entity = getOrderV2Entity();
        var title = "Friend";
        var postAddressV2Entity = getPostAddressV2Entity();
        var expectedPostAddressEntity = getPostAddressV2EntityWithNewTitle();
        var expectedOrderV2Entity = getOrderV2EntityWithId();

        when(mapper.toEntity(orderV2)).thenReturn(orderV2Entity);
        when(accountJpaAdapter.getReferenceById(accountId)).thenReturn(getAccountEntity());
        when(postAddressJpaAdapter.findByTitleAndAccount_Id("permanent: " + title, accountId)).thenReturn(Optional.empty());
        when(postAddressMapper.toEntity(getPostAddressV2WithNewRecipientInfo())).thenReturn(postAddressV2Entity);
        when(postAddressJpaAdapter.save(any(PostAddressV2Entity.class))).thenReturn(expectedPostAddressEntity);
        when(productJpaAdapter.getReferenceById(orderV2Entity.getOrderItems().get(0).getProduct().getId())).thenReturn(getProductEntity());
        when(jpaAdapter.save(any(OrderV2Entity.class))).thenReturn(expectedOrderV2Entity);
        doNothing().when(orderV1DuplicateAdapter).duplicateOrderV2ToV1(expectedOrderV2Entity);

        //When
        var actualUUID = repository.save(orderV2, accountId);

        //Then
        assertEquals(actualUUID, expectedOrderV2Entity.getId());
    }

    @Test
    void save_FoundExistingPostAddressWithGivenTitleAndTheSameWithInputFields_Test() {
        //Given
        var orderV2 = getOrderV2WithoutIdWithNewRecipientInfo();
        var accountId = 1L;
        var orderV2Entity = getOrderV2Entity();
        var title = "Friend";
        var expectedOrderV2Entity = getOrderV2EntityWithId();

        when(mapper.toEntity(orderV2)).thenReturn(orderV2Entity);
        when(accountJpaAdapter.getReferenceById(accountId)).thenReturn(getAccountEntity());
        when(postAddressJpaAdapter.findByTitleAndAccount_Id("permanent: " + title, accountId)).thenReturn(Optional.of(getPostAddressV2EntityWithNewTitle()));
        when(productJpaAdapter.getReferenceById(orderV2Entity.getOrderItems().get(0).getProduct().getId())).thenReturn(getProductEntity());
        when(jpaAdapter.save(any(OrderV2Entity.class))).thenReturn(expectedOrderV2Entity);
        doNothing().when(orderV1DuplicateAdapter).duplicateOrderV2ToV1(expectedOrderV2Entity);

        //When
        var actualUUID = repository.save(orderV2, accountId);

        //Then
        assertEquals(actualUUID, expectedOrderV2Entity.getId());
    }

    @Test
    void save_FoundExistingPostAddressWithGivenTitleAndDifferentWithInputFields_Test() {
        //Given
        var orderV2 = getOrderV2WithoutId();
        var accountId = 1L;
        var orderV2Entity = getOrderV2Entity();
        var title = "Friend";

        when(mapper.toEntity(orderV2)).thenReturn(orderV2Entity);
        when(accountJpaAdapter.getReferenceById(accountId)).thenReturn(getAccountEntity());
        when(postAddressJpaAdapter.findByTitleAndAccount_Id("permanent: " + title, accountId)).thenReturn(Optional.of(getPostAddressV2EntityWithNewTitle()));

        //When
        PostAddressTitleAlreadyExistsException exception = assertThrows(PostAddressTitleAlreadyExistsException.class,
                () -> repository.save(orderV2, accountId));

        //Then
        assertEquals("This PostAddress title already exists: " + title, exception.getMessage());

        verifyNoInteractions(productJpaAdapter);
        verifyNoInteractions(jpaAdapter);
        verifyNoInteractions(orderV1DuplicateAdapter);
    }
}
