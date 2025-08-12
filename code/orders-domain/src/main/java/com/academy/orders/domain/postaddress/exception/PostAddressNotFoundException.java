package com.academy.orders.domain.postaddress.exception;

import com.academy.orders.domain.common.exception.NotFoundException;
import lombok.Getter;

import java.util.UUID;

@Getter
public class PostAddressNotFoundException extends NotFoundException {
  private final UUID addressId;

  public PostAddressNotFoundException(UUID addressId) {
    super(String.format("PostAddress with id: %s is not found", addressId));
    this.addressId = addressId;
  }
}
