package com.academy.orders.domain.order.exception;

import com.academy.orders.domain.common.exception.NotFoundException;
import lombok.Getter;

import java.io.Serial;
import java.util.UUID;

public class OrderNotFoundException extends NotFoundException {

  @Serial
  private static final long serialVersionUID = 90712755442212371L;

  @Getter
  private final UUID orderId;

  public OrderNotFoundException(UUID orderId) {
    super(String.format("Order with ID: %s is not found", orderId));
    this.orderId = orderId;
  }
}
