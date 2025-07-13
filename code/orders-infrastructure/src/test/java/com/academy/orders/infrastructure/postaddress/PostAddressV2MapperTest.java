package com.academy.orders.infrastructure.postaddress;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static com.academy.orders.infrastructure.ModelUtils.getPostAddressV2;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class PostAddressV2MapperTest {
    private PostAddressV2Mapper mapper;

    @BeforeEach
    public void setUp() {mapper = Mappers.getMapper(PostAddressV2Mapper.class);}

    @Test
    void toEntityTest() {
        //Given
        var postAddressV2 = getPostAddressV2();

        //When
        var postAddressV2Entity = mapper.toEntity(postAddressV2);

        //Then
        assertNotNull(postAddressV2Entity);
        assertEquals(postAddressV2Entity.getCity(), postAddressV2.city());
        assertEquals(postAddressV2Entity.getAccount().getId(), postAddressV2.account().id());
        assertEquals(postAddressV2Entity.getTitle(), postAddressV2.title());
        assertEquals(postAddressV2Entity.getOrders().size(), postAddressV2.orders().size());
        assertEquals(postAddressV2Entity.getDepartment(), postAddressV2.department());
        assertEquals(postAddressV2Entity.getDeliveryMethod(), postAddressV2.deliveryMethod());
        assertEquals(postAddressV2Entity.getRecipientFirstName(), postAddressV2.recipientFirstName());
        assertEquals(postAddressV2Entity.getRecipientLastName(), postAddressV2.recipientLastName());
        assertEquals(postAddressV2Entity.getRecipientPhone(), postAddressV2.recipientPhone());
    }
}
