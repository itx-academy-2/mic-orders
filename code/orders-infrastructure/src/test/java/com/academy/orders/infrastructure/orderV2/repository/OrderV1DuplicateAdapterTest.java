package com.academy.orders.infrastructure.orderV2.repository;

import com.academy.orders.domain.order.entity.enumerated.DeliveryMethod;
import com.academy.orders.domain.order.entity.enumerated.OrderStatus;
import com.academy.orders.infrastructure.account.entity.AccountEntity;
import com.academy.orders.infrastructure.orderV2.entity.OrderItemV2Entity;
import com.academy.orders.infrastructure.orderV2.entity.OrderV2Entity;
import com.academy.orders.infrastructure.postaddress.entity.PostAddressV2Entity;
import com.academy.orders.infrastructure.product.entity.ProductEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.atLeastOnce;

@ExtendWith(MockitoExtension.class)
public class OrderV1DuplicateAdapterTest {
  @Mock
  private EntityManager entityManager;

  @Mock
  private Query query;

  @InjectMocks
  private OrderV1DuplicateAdapter adapter;

  @Test
  void duplicateOrderV2ToV1_Test() {
    // Given
    var order = mock(OrderV2Entity.class);
    var address = mock(PostAddressV2Entity.class);
    var account = mock(AccountEntity.class);
    var item = mock(OrderItemV2Entity.class);
    var items = List.of(item);

    var orderId = UUID.randomUUID();
    var productId = UUID.randomUUID();
    var createdAt = LocalDateTime.of(2025, 5, 23, 13, 30, 0);

    when(order.getId()).thenReturn(orderId);
    when(order.getIsPaid()).thenReturn(true);
    when(order.getOrderStatus()).thenReturn(OrderStatus.IN_PROGRESS);
    when(order.getCreatedAt()).thenReturn(createdAt);
    when(order.getEditedAt()).thenReturn(null);
    when(order.getAccount()).thenReturn(account);
    when(order.getPostAddress()).thenReturn(address);
    when(order.getOrderItems()).thenReturn(items);

    when(item.getProduct()).thenReturn(mock(ProductEntity.class));
    when(item.getProduct().getId()).thenReturn(productId);
    when(item.getPrice()).thenReturn(BigDecimal.valueOf(300.8));
    when(item.getDiscount()).thenReturn(null);
    when(item.getQuantity()).thenReturn(1);

    when(account.getId()).thenReturn(1L);
    when(account.getEmail()).thenReturn("test@example.com");

    when(address.getRecipientFirstName()).thenReturn("Test");
    when(address.getRecipientLastName()).thenReturn("User");
    when(address.getDeliveryMethod()).thenReturn(DeliveryMethod.NOVA);
    when(address.getCity()).thenReturn("Kharkiv");
    when(address.getDepartment()).thenReturn("№1");

    when(entityManager.createNativeQuery(anyString())).thenReturn(query);
    when(query.setParameter(anyInt(), any())).thenReturn(query);
    when(query.executeUpdate()).thenReturn(1);

    // When
    adapter.duplicateOrderV2ToV1(order);

    // Then
    verify(query, atLeastOnce()).setParameter(1, orderId);
    verify(query, atLeastOnce()).setParameter(2, true);
    verify(query).setParameter(3, OrderStatus.IN_PROGRESS.name());
    verify(query).setParameter(4, createdAt);
    verify(query).setParameter(5, null);
    verify(query).setParameter(6, 1L);
    verify(query).setParameter(7, "Test");
    verify(query).setParameter(8, "User");
    verify(query).setParameter(9, "test@example.com");

    verify(entityManager, atLeastOnce()).createNativeQuery(anyString());
    verify(query, atLeastOnce()).setParameter(anyInt(), any());
    verify(query, atLeastOnce()).executeUpdate();
  }
}
