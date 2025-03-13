package com.academy.orders.application.product.usecase;

import com.academy.orders.domain.common.Pageable;
import com.academy.orders.domain.product.dto.ProductsOnSaleFilterDto;
import com.academy.orders.domain.product.entity.Product;
import com.academy.orders.domain.product.repository.ProductRepository;
import com.academy.orders.domain.product.usecase.ProductFilterUsageMetricsUseCase;
import com.academy.orders.domain.product.usecase.SetPercentageOfTotalOrdersUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static com.academy.orders.application.ModelUtils.getDiscountAndPriceWithDiscountRangeDto;
import static com.academy.orders.application.ModelUtils.getPage;
import static com.academy.orders.application.ModelUtils.getProductWithImageUrlAndAppliedDiscount;
import static com.academy.orders.application.ModelUtils.getProductWithImageUrlAndDiscount;
import static com.academy.orders.application.TestConstants.LANGUAGE_EN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GetProductsOnSaleUseCaseTest {
  @InjectMocks
  private GetProductsOnSaleUseCaseImpl getProductsOnSaleUseCase;

  @Mock
  private ProductRepository productRepository;

  @Mock
  private SetPercentageOfTotalOrdersUseCase setPercentageOfTotalOrdersUseCase;

  @Mock
  private ProductFilterUsageMetricsUseCase productFilterUsageMetricsUseCase;

  @Test
  void getProductsOnSaleTest() {
    var lang = LANGUAGE_EN;
    var size = 5;
    var pageable = Pageable.builder().page(0).size(size).build();
    var product = getProductWithImageUrlAndDiscount();
    var filter = ProductsOnSaleFilterDto.builder().maximumPriceWithDiscount(BigDecimal.valueOf(1000)).build();
    var expectedProduct = getProductWithImageUrlAndAppliedDiscount();
    var range = getDiscountAndPriceWithDiscountRangeDto();
    var page = getPage(List.of(product), 1L, 1, 0, size);

    when(productRepository.findProductsWhereDiscountIsNotNull(filter, lang, pageable)).thenReturn(page);
    when(productRepository.findDiscountAndPriceWithDiscountRange()).thenReturn(range);
    doNothing().when(setPercentageOfTotalOrdersUseCase).setPercentOfTotalOrders(anyList());
    doNothing().when(productFilterUsageMetricsUseCase).addMetrics(filter);

    var actual = getProductsOnSaleUseCase.getProductsOnSale(filter, pageable, lang);

    assertNotNull(actual);
    assertEquals(1, actual.pageProducts().content().size());
    assertEquals(expectedProduct, actual.pageProducts().content().get(0));
    assertEquals(range.minimumDiscount(), actual.minimumDiscount());
    assertEquals(range.maximumDiscount(), actual.maximumDiscount());
    assertEquals(range.minimumPriceWithDiscount(), actual.minimumPriceWithDiscount());
    assertEquals(range.maximumPriceWithDiscount(), actual.maximumPriceWithDiscount());

    verify(productRepository).findProductsWhereDiscountIsNotNull(filter, lang, pageable);
    verify(productRepository).findDiscountAndPriceWithDiscountRange();
    verify(productFilterUsageMetricsUseCase).addMetrics(filter);
    verify(setPercentageOfTotalOrdersUseCase).setPercentOfTotalOrders(anyList());
  }

  @Test
  void getProductsOnSaleReturnsEmptyPageTest() {
    var lang = LANGUAGE_EN;
    var size = 5;
    var pageable = Pageable.builder().page(0).size(size).build();
    var filter = ProductsOnSaleFilterDto.builder().maximumPriceWithDiscount(BigDecimal.ZERO).build();
    var range = getDiscountAndPriceWithDiscountRangeDto();

    var page = getPage(new ArrayList<Product>(), 0L, 0, 0, size);

    when(productRepository.findProductsWhereDiscountIsNotNull(filter, lang, pageable)).thenReturn(page);
    when(productRepository.findDiscountAndPriceWithDiscountRange()).thenReturn(range);
    doNothing().when(setPercentageOfTotalOrdersUseCase).setPercentOfTotalOrders(anyList());
    doNothing().when(productFilterUsageMetricsUseCase).addMetrics(filter);

    var actual = getProductsOnSaleUseCase.getProductsOnSale(filter, pageable, lang);

    assertNotNull(actual);
    assertEquals(range.minimumPriceWithDiscount(), actual.minimumPriceWithDiscount());
    assertEquals(range.maximumPriceWithDiscount(), actual.maximumPriceWithDiscount());
    assertEquals(range.minimumDiscount(), actual.minimumDiscount());
    assertEquals(range.maximumDiscount(), actual.maximumDiscount());
    assertEquals(0, actual.pageProducts().content().size());

    verify(productRepository).findProductsWhereDiscountIsNotNull(filter, lang, pageable);
    verify(productRepository).findDiscountAndPriceWithDiscountRange();
    verify(productFilterUsageMetricsUseCase).addMetrics(filter);
    verify(setPercentageOfTotalOrdersUseCase).setPercentOfTotalOrders(anyList());
  }
}
