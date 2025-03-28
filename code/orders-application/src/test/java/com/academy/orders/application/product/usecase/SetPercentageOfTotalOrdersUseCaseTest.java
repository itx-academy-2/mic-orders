package com.academy.orders.application.product.usecase;

import com.academy.orders.domain.product.dto.ProductBestsellersDto;
import com.academy.orders.domain.product.entity.Product;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.academy.orders.application.ModelUtils.getProductWithImageLink;
import static com.academy.orders.domain.filter.FilterMetricsConstants.AMOUNT_OF_MOST_SOLD_ITEMS;
import static com.academy.orders.domain.filter.FilterMetricsConstants.DAYS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SetPercentageOfTotalOrdersUseCaseTest {
  @Mock
  private GetProductBestsellersDtoUseCaseImpl getProductBestsellersUseCase;

  @InjectMocks
  private SetPercentageOfTotalOrdersUseCaseImpl setPercentageOfTotalOrdersUseCase;

  @Test
  void setPercentOfTotalOrdersWhenProductsAreNotNull() {
    final Product product = getProductWithImageLink();
    final ProductBestsellersDto productBestsellersDto = new ProductBestsellersDto(product.getId(), 20.0);
    final List<ProductBestsellersDto> list = List.of(productBestsellersDto);

    when(getProductBestsellersUseCase.getProductBestsellers(DAYS, AMOUNT_OF_MOST_SOLD_ITEMS)).thenReturn(list);

    setPercentageOfTotalOrdersUseCase.setPercentOfTotalOrders(product);

    assertEquals(productBestsellersDto.percentageOfTotalOrders(), product.getPercentageOfTotalOrders());

    verify(getProductBestsellersUseCase).getProductBestsellers(DAYS, AMOUNT_OF_MOST_SOLD_ITEMS);
  }

  @Test
  void setPercentOfTotalOrdersWhenProductIsNotNull() {
    final Product product = getProductWithImageLink();
    final List<Product> productsList = List.of(product);
    final ProductBestsellersDto productBestsellersDto = new ProductBestsellersDto(product.getId(), 20.0);
    final List<ProductBestsellersDto> list = List.of(productBestsellersDto);

    when(getProductBestsellersUseCase.getProductBestsellers(DAYS, AMOUNT_OF_MOST_SOLD_ITEMS)).thenReturn(list);

    setPercentageOfTotalOrdersUseCase.setPercentOfTotalOrders(productsList);

    assertEquals(productBestsellersDto.percentageOfTotalOrders(), product.getPercentageOfTotalOrders());

    verify(getProductBestsellersUseCase).getProductBestsellers(DAYS, AMOUNT_OF_MOST_SOLD_ITEMS);
  }

  @Test
  void setPercentOfTotalOrdersWhenProductIsNull() {
    final Product product = null;

    setPercentageOfTotalOrdersUseCase.setPercentOfTotalOrders(product);

    verify(getProductBestsellersUseCase, never()).getProductBestsellers(DAYS, AMOUNT_OF_MOST_SOLD_ITEMS);
  }

  @Test
  void setPercentOfTotalOrdersWhenProductsAreNull() {
    final List<Product> list = null;

    setPercentageOfTotalOrdersUseCase.setPercentOfTotalOrders(list);

    verify(getProductBestsellersUseCase, never()).getProductBestsellers(DAYS, AMOUNT_OF_MOST_SOLD_ITEMS);
  }
}
