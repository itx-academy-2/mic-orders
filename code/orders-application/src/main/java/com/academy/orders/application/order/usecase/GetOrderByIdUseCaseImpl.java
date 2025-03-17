package com.academy.orders.application.order.usecase;

import com.academy.orders.domain.order.entity.Order;
import com.academy.orders.domain.order.entity.OrderItem;
import com.academy.orders.domain.order.exception.OrderNotFoundException;
import com.academy.orders.domain.order.repository.OrderRepository;
import com.academy.orders.domain.order.usecase.CalculateOrderTotalPriceUseCase;
import com.academy.orders.domain.order.usecase.GetOrderByIdUseCase;
import com.academy.orders.domain.product.usecase.SetPercentageOfTotalOrdersUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetOrderByIdUseCaseImpl implements GetOrderByIdUseCase {
  private final CalculateOrderTotalPriceUseCase calculateOrderTotalPriceUseCase;

  private final OrderRepository orderRepository;

  private final SetPercentageOfTotalOrdersUseCase setPercentageOfTotalOrdersUseCase;

  @Override
  public Order getOrderById(UUID id, String language) {
    final Order order = calculateOrderTotalPriceUseCase.calculateTotalPriceFor(
        orderRepository.findById(id, language).orElseThrow(() -> new OrderNotFoundException(id)));
    order.orderItems().stream()
        .map(OrderItem::product)
        .forEach(setPercentageOfTotalOrdersUseCase::setPercentOfTotalOrders);
    return order;
  }
}
