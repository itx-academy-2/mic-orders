package com.academy.orders.application.order.usecase;

import com.academy.orders.domain.common.Page;
import com.academy.orders.domain.common.Pageable;
import com.academy.orders.domain.order.dto.OrdersFilterParametersDto;
import com.academy.orders.domain.order.entity.Order;
import com.academy.orders.domain.order.entity.OrderManagement;
import com.academy.orders.domain.order.repository.OrderRepository;
import com.academy.orders.domain.order.usecase.CalculateOrderTotalPriceUseCase;
import com.academy.orders.domain.order.usecase.GetAllOrdersUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetAllOrdersUseCaseImpl implements GetAllOrdersUseCase {
  private final OrderRepository orderRepository;

  private final CalculateOrderTotalPriceUseCase calculateOrderTotalPriceUseCase;

  @Override
  public Page<OrderManagement> getAllOrders(OrdersFilterParametersDto filterParametersDto, Pageable pageable,
      String role) {
    if (pageable.sort().isEmpty()) {
      pageable = new Pageable(pageable.page(), pageable.size(), List.of("createdAt,desc"));
    }
    Page<Order> orderPage = orderRepository.findAll(filterParametersDto, pageable);

    return Page.<OrderManagement>builder().totalElements(orderPage.totalElements())
        .totalPages(orderPage.totalPages()).first(orderPage.first()).last(orderPage.last())
        .number(orderPage.number()).numberOfElements(orderPage.numberOfElements()).size(orderPage.size())
        .empty(orderPage.empty()).content(calculateOrderTotalPriceUseCase
            .calculateTotalPriceAndAvailableStatuses(orderPage.content(), role))
        .build();
  }

}
