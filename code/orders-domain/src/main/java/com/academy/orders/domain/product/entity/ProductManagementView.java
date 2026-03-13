package com.academy.orders.domain.product.entity;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Builder
@Getter
@EqualsAndHashCode
public class ProductManagementView {

  private Product product;

  private Integer reservedQuantity;

}
