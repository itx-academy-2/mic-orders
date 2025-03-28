package com.academy.orders.application.product.usecase;

import com.academy.orders.domain.common.Pageable;
import com.academy.orders.domain.product.dto.ProductBestsellersDto;
import com.academy.orders.domain.product.dto.ProductsOnSaleFilterDto;
import com.academy.orders.domain.product.entity.Product;
import com.academy.orders.domain.product.repository.ProductRepository;
import com.academy.orders.domain.product.usecase.GetProductBestsellersUseCase;
import com.academy.orders.domain.product.usecase.ProductFilterUsageMetricsUseCase;
import com.academy.orders.domain.product.usecase.SetPercentageOfTotalOrdersUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.academy.orders.application.ModelUtils.getDiscountAndPriceWithDiscountRangeDto;
import static com.academy.orders.application.ModelUtils.getPage;
import static com.academy.orders.application.ModelUtils.getProductWithImageUrlAndAppliedDiscount;
import static com.academy.orders.application.ModelUtils.getProductWithImageUrlAndDiscount;
import static com.academy.orders.application.TestConstants.LANGUAGE_EN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
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
  private GetProductBestsellersUseCase getProductBestsellersUseCase;

  @Mock
  private SetPercentageOfTotalOrdersUseCase setPercentageOfTotalOrdersUseCase;

  @Mock
  private ProductFilterUsageMetricsUseCase productFilterUsageMetricsUseCase;

  @Captor
  private ArgumentCaptor<List<UUID>> bestsellersIdsCaptor;

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
    var productBestsellersDtos = List.of(new ProductBestsellersDto(UUID.fromString("f3786b85-0d0e-4bff-92f8-6f9d0b3dc6f5"), 45.68));

    when(getProductBestsellersUseCase.getProductBestsellers(30, 5)).thenReturn(productBestsellersDtos);
    when(productRepository.findProductsWhereDiscountIsNotNull(eq(filter), eq(lang), eq(pageable), anyList())).thenReturn(page);
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

    verify(productRepository).findProductsWhereDiscountIsNotNull(eq(filter), eq(lang), eq(pageable), bestsellersIdsCaptor.capture());
    verify(productRepository).findDiscountAndPriceWithDiscountRange();
    verify(productFilterUsageMetricsUseCase).addMetrics(filter);
    verify(setPercentageOfTotalOrdersUseCase).setPercentOfTotalOrders(page.content());

    final List<UUID> bestsellersIds = bestsellersIdsCaptor.getValue();
    assertNotNull(bestsellersIds);
    assertEquals(productBestsellersDtos.size(), bestsellersIds.size());

    for (int i = 0; i < productBestsellersDtos.size(); i++) {
      assertEquals(productBestsellersDtos.get(i).productId(), bestsellersIds.get(i));
    }
  }

  @Test
  void getProductsOnSaleReturnsEmptyPageTest() {
    var lang = LANGUAGE_EN;
    var size = 5;
    var pageable = Pageable.builder().page(0).size(size).build();
    var filter = ProductsOnSaleFilterDto.builder().maximumPriceWithDiscount(BigDecimal.ZERO).build();
    var range = getDiscountAndPriceWithDiscountRangeDto();
    var productBestsellersDtos = List.of(
        new ProductBestsellersDto(UUID.fromString("f3786b85-0d0e-4bff-92f8-6f9d0b3dc6f5"), 45.68),
        new ProductBestsellersDto(UUID.fromString("f3786b85-0d0e-4bff-92f8-6f5d0b3dc6f5"), 42.68));
    var page = getPage(new ArrayList<Product>(), 0L, 0, 0, size);

    when(getProductBestsellersUseCase.getProductBestsellers(30, 5)).thenReturn(productBestsellersDtos);
    when(productRepository.findProductsWhereDiscountIsNotNull(eq(filter), eq(lang), eq(pageable), anyList())).thenReturn(page);
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

    verify(productRepository).findProductsWhereDiscountIsNotNull(eq(filter), eq(lang), eq(pageable), bestsellersIdsCaptor.capture());
    verify(productRepository).findDiscountAndPriceWithDiscountRange();
    verify(productFilterUsageMetricsUseCase).addMetrics(filter);
    verify(setPercentageOfTotalOrdersUseCase).setPercentOfTotalOrders(page.content());

    final List<UUID> bestsellersIds = bestsellersIdsCaptor.getValue();
    assertNotNull(bestsellersIds);
    assertEquals(productBestsellersDtos.size(), bestsellersIds.size());

    for (int i = 0; i < productBestsellersDtos.size(); i++) {
      assertEquals(productBestsellersDtos.get(i).productId(), bestsellersIds.get(i));
    }
  }
}
