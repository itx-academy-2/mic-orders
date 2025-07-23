package com.academy.orders.infrastructure.orderV2;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static com.academy.orders.infrastructure.ModelUtils.getOrderV2;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class OrderV2MapperTest {
  private OrderV2Mapper mapper;

  @BeforeEach
  public void setUp() {
    mapper = Mappers.getMapper(OrderV2Mapper.class);
  }

  @Test
  void toEntityTest() {
    // Given
    var orderV2 = getOrderV2();

    // When
    var orderV2Entity = mapper.toEntity(orderV2);

    // Then
    assertNotNull(orderV2Entity);
    assertEquals(orderV2.orderStatus(), orderV2Entity.getOrderStatus());
    assertEquals(orderV2.postAddress().title(), orderV2Entity.getPostAddress().getTitle());
    assertEquals(orderV2.postAddress().city(), orderV2Entity.getPostAddress().getCity());
    assertEquals(orderV2.account().id(), orderV2Entity.getAccount().getId());
    assertEquals(orderV2.orderItems().size(), orderV2Entity.getOrderItems().size());
    assertEquals(orderV2.isPaid(), orderV2Entity.getIsPaid());
    assertEquals(orderV2.createdAt(), orderV2Entity.getCreatedAt());
  }
}
