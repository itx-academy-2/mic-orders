package com.academy.orders.application.order.usecase;

import com.academy.orders.application.ModelUtils;
import com.academy.orders.domain.account.entity.enumerated.Role;
import com.academy.orders.domain.common.Page;
import com.academy.orders.domain.common.Pageable;
import com.academy.orders.domain.order.dto.OrdersFilterParametersDto;
import com.academy.orders.domain.order.entity.Order;
import com.academy.orders.domain.order.entity.OrderManagement;
import com.academy.orders.domain.order.repository.OrderRepository;
import com.academy.orders.domain.order.usecase.CalculateOrderTotalPriceUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.academy.orders.application.ModelUtils.getOrderManagementForManager;
import static com.academy.orders.application.ModelUtils.getOrderWithoutTotal;
import static com.academy.orders.application.ModelUtils.getPageOf;
import static com.academy.orders.application.ModelUtils.getPageable;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetAllOrdersUseCaseTest {
  @InjectMocks
  private GetAllOrdersUseCaseImpl getAllOrdersUseCase;

  @Mock
  private OrderRepository orderRepository;

  @Mock
  private CalculateOrderTotalPriceUseCase calculateOrderTotalPriceUseCase;

  @Test
  void getOrdersByUserIdTest() {
    // Given
    OrdersFilterParametersDto filterParametersDto = ModelUtils.getOrdersFilterParametersDto();
    Pageable pageable = getPageable();
    Order withoutTotal = getOrderWithoutTotal();
    OrderManagement withTotal = getOrderManagementForManager();
    Page<Order> orderPage = getPageOf(withoutTotal);
    Page<OrderManagement> expected = getPageOf(withTotal);

    when(orderRepository.findAll(filterParametersDto, pageable)).thenReturn(orderPage);
    when(calculateOrderTotalPriceUseCase.calculateTotalPriceAndAvailableStatuses(orderPage.content(),
        String.valueOf(Role.ROLE_MANAGER))).thenReturn(expected.content());

    // When
    Page<OrderManagement> ordersByUserId = getAllOrdersUseCase.getAllOrders(filterParametersDto, pageable,
        String.valueOf(Role.ROLE_MANAGER));

    // Then
    assertEquals(expected, ordersByUserId);
    verify(orderRepository).findAll(filterParametersDto, pageable);
    verify(calculateOrderTotalPriceUseCase).calculateTotalPriceAndAvailableStatuses(orderPage.content(),
        String.valueOf(Role.ROLE_MANAGER));
  }

  @Test
  void getOrdersByUserIdWithEmptySortTest() {
    // Given
    OrdersFilterParametersDto filterParametersDto = ModelUtils.getOrdersFilterParametersDto();
    Pageable pageable = new Pageable(0, 8, List.of());
    Pageable defaultPageable = new Pageable(0, 8, List.of("createdAt,desc"));
    Order withoutTotal = getOrderWithoutTotal();
    OrderManagement withTotal = getOrderManagementForManager();
    Page<Order> orderPage = getPageOf(withoutTotal);
    Page<OrderManagement> expected = getPageOf(withTotal);

    when(orderRepository.findAll(filterParametersDto, defaultPageable)).thenReturn(orderPage);
    when(calculateOrderTotalPriceUseCase.calculateTotalPriceAndAvailableStatuses(orderPage.content(),
        String.valueOf(Role.ROLE_MANAGER))).thenReturn(expected.content());

    // When
    Page<OrderManagement> ordersByUserId = getAllOrdersUseCase.getAllOrders(filterParametersDto, pageable,
        String.valueOf(Role.ROLE_MANAGER));

    // Then
    assertEquals(expected, ordersByUserId);
    verify(orderRepository).findAll(filterParametersDto, defaultPageable);
  }
}
