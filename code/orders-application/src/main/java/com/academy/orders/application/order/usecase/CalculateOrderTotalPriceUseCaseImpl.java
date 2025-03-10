package com.academy.orders.application.order.usecase;

import com.academy.orders.domain.order.entity.Order;
import com.academy.orders.domain.order.entity.OrderItem;
import com.academy.orders.domain.order.entity.OrderManagement;
import com.academy.orders.domain.order.entity.enumerated.OrderStatus;
import com.academy.orders.domain.order.usecase.CalculateOrderTotalPriceUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CalculateOrderTotalPriceUseCaseImpl implements CalculateOrderTotalPriceUseCase {
  private static final String ROLE_ADMIN = "role_admin";

  @Override
  public BigDecimal calculateTotalPrice(List<OrderItem> orderItems) {
    return orderItems.stream().map(OrderItem::price).reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  @Override
  public List<Order> calculateTotalPriceFor(List<Order> orders) {
    if (orders == null) {
      return Collections.emptyList();
    }
    return orders.stream()
        .map(order -> Order.builder().id(order.id()).orderStatus(order.orderStatus()).receiver(order.receiver())
            .postAddress(order.postAddress()).total(calculateTotalPrice(order.orderItems()))
            .orderItems(order.orderItems()).isPaid(order.isPaid()).editedAt(order.editedAt())
            .account(order.account()).createdAt(order.createdAt()).build())
        .toList();
  }

  @Override
  public Order calculateTotalPriceFor(Order order) {
    return calculateTotalPriceFor(List.of(order)).get(0);
  }

  @Override
  public List<OrderManagement> calculateTotalPriceAndAvailableStatuses(List<Order> orders, String role) {
    if (orders == null) {
      return Collections.emptyList();
    }
    boolean isAdmin = ROLE_ADMIN.equalsIgnoreCase(String.valueOf(role));

    return orders.stream()
        .map(order -> OrderManagement.builder().id(order.id()).orderStatus(order.orderStatus())
            .availableStatuses(getAvailableStatuses(isAdmin, order)).receiver(order.receiver())
            .postAddress(order.postAddress()).total(calculateTotalPrice(order.orderItems()))
            .orderItems(order.orderItems()).isPaid(order.isPaid()).editedAt(order.editedAt())
            .account(order.account()).createdAt(order.createdAt()).build())
        .toList();

  }

  private List<OrderStatus> getAvailableStatuses(boolean isAdmin, Order order) {
    return isAdmin
        ? OrderStatus.getAllTransitions()
        : OrderStatus.getAllowedTransitions(order.orderStatus(), false);
  }
}
