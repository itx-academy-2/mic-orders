package com.academy.orders.application.product.usecase;

import com.academy.orders.domain.cart.dto.CartItemDto;
import com.academy.orders.domain.cart.entity.CartItem;
import com.academy.orders.domain.product.dto.ProductBestsellersDto;
import com.academy.orders.domain.product.entity.Product;
import com.academy.orders.domain.product.usecase.GetProductBestsellersUseCase;
import com.academy.orders.domain.product.usecase.SetPercentageOfTotalOrdersUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.academy.orders.domain.filter.FilterMetricsConstants.AMOUNT_OF_MOST_SOLD_ITEMS;
import static com.academy.orders.domain.filter.FilterMetricsConstants.DAYS;

@Service
@RequiredArgsConstructor
public class SetPercentageOfTotalOrdersUseCaseImpl implements SetPercentageOfTotalOrdersUseCase {
  private final GetProductBestsellersUseCase getProductBestsellersUseCase;

  @Override
  public void setPercentOfTotalOrders(final List<Product> list) {
    if (list == null || list.isEmpty()) {
      return;
    }
    final Map<UUID, Double> bestsellersMap = getProductBestsellersUseCase.getProductBestsellers(DAYS, AMOUNT_OF_MOST_SOLD_ITEMS)
        .stream()
        .collect(Collectors.toMap(ProductBestsellersDto::productId, ProductBestsellersDto::percentageOfTotalOrders));

    list.forEach(product -> product.setPercentageOfTotalOrders(bestsellersMap.get(product.getId())));
  }

  @Override
  public void setPercentOfTotalOrders(final Product product) {
    if (product == null) {
      return;
    }
    getProductBestsellersUseCase.getProductBestsellers(DAYS, AMOUNT_OF_MOST_SOLD_ITEMS).stream()
        .filter(o -> o.productId().equals(product.getId()))
        .findFirst()
        .ifPresent(o -> product.setPercentageOfTotalOrders(o.percentageOfTotalOrders()));
  }
}
