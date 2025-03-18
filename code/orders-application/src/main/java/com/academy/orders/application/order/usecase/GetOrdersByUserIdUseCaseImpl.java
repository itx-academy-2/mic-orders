package com.academy.orders.application.order.usecase;

import com.academy.orders.domain.common.Page;
import com.academy.orders.domain.common.Pageable;
import com.academy.orders.domain.order.entity.Order;
import com.academy.orders.domain.order.entity.OrderItem;
import com.academy.orders.domain.order.repository.OrderRepository;
import com.academy.orders.domain.order.usecase.CalculateOrderTotalPriceUseCase;
import com.academy.orders.domain.order.usecase.GetOrdersByUserIdUseCase;
import com.academy.orders.domain.product.usecase.SetPercentageOfTotalOrdersUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetOrdersByUserIdUseCaseImpl implements GetOrdersByUserIdUseCase {
  private final OrderRepository orderRepository;

  private final CalculateOrderTotalPriceUseCase calculateOrderTotalPriceUseCase;

  private final SetPercentageOfTotalOrdersUseCase setPercentageOfTotalOrdersUseCase;

  @Override
  public Page<Order> getOrdersByUserId(Long id, String language, Pageable pageable) {
    Page<Order> orderPage = orderRepository.findAllByUserId(id, language, pageable);
    orderPage.content().stream()
        .flatMap(o -> o.orderItems().stream().map(OrderItem::product))
        .forEach(setPercentageOfTotalOrdersUseCase::setPercentOfTotalOrders);

    return buildPage(orderPage);
  }

  private Page<Order> buildPage(Page<Order> orderPage) {
    return Page.<Order>builder().totalElements(orderPage.totalElements()).totalPages(orderPage.totalPages())
        .first(orderPage.first()).last(orderPage.last()).number(orderPage.number())
        .numberOfElements(orderPage.numberOfElements()).size(orderPage.size()).empty(orderPage.empty())
        .content(calculateOrderTotalPriceUseCase.calculateTotalPriceFor(orderPage.content())).build();
  }
}
