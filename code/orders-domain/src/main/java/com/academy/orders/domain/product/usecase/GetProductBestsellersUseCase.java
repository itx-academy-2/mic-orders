package com.academy.orders.domain.product.usecase;

import com.academy.orders.domain.product.dto.ProductBestsellersDto;

import java.util.List;

public interface GetProductBestsellersUseCase {
  List<ProductBestsellersDto> getProductBestsellers(int days, int quantity);
}
