package com.academy.orders.application.product.usecase;

import com.academy.orders.domain.common.Pageable;
import com.academy.orders.domain.product.dto.ProductBestsellersDto;
import com.academy.orders.domain.product.entity.Product;
import com.academy.orders.domain.product.repository.ProductRepository;
import com.academy.orders.domain.product.usecase.GetProductBestsellersUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static com.academy.orders.application.ModelUtils.getPage;
import static com.academy.orders.application.ModelUtils.getProductWithImageLink;
import static com.academy.orders.application.TestConstants.LANGUAGE_UK;
import static com.academy.orders.domain.filter.FilterMetricsConstants.AMOUNT_OF_MOST_SOLD_ITEMS;
import static com.academy.orders.domain.filter.FilterMetricsConstants.DAYS;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetAllProductsUseCaseTest {
  @Mock
  private ProductRepository productRepository;

  @Mock
  private GetProductBestsellersUseCase getProductBestsellersUseCase;

  @Mock
  private SetPercentageOfTotalOrdersUseCaseImpl setPercentageOfTotalOrdersUseCase;

  @InjectMocks
  private GetAllProductsUseCaseImpl getAllProductsUseCase;

  @Captor
  private ArgumentCaptor<List<UUID>> bestsellersIdsCaptor;

  @Test
  void getAllProductsTest() {
    var pageable = Pageable.builder().page(0).size(10).sort(List.of("name,desc")).build();
    var product = getProductWithImageLink();
    var expectedProducts = singletonList(product);
    var expectedPage = getPage(expectedProducts, 1L, 1, 0, 10);
    List<String> tags = Collections.emptyList();
    var productBestsellersDtos = List.of(new ProductBestsellersDto(UUID.fromString("f3786b85-0d0e-4bff-92f8-6f9d0b3dc6f5"), 45.68));

    when(getProductBestsellersUseCase.getProductBestsellers(DAYS, AMOUNT_OF_MOST_SOLD_ITEMS)).thenReturn(productBestsellersDtos);
    when(productRepository.findAllProducts(eq(LANGUAGE_UK), eq(pageable), eq(tags), anyList())).thenReturn(expectedPage);
    doNothing().when(setPercentageOfTotalOrdersUseCase).setPercentOfTotalOrders(anyList());

    var actualPage = getAllProductsUseCase.getAllProducts(LANGUAGE_UK, pageable, tags);

    assertEquals(expectedPage, actualPage);
    verify(productRepository).findAllProducts(eq(LANGUAGE_UK), eq(pageable), eq(tags), bestsellersIdsCaptor.capture());
    verify(setPercentageOfTotalOrdersUseCase).setPercentOfTotalOrders(expectedProducts);
    verify(getProductBestsellersUseCase).getProductBestsellers(DAYS, AMOUNT_OF_MOST_SOLD_ITEMS);

    final List<UUID> bestsellersIds = bestsellersIdsCaptor.getValue();
    assertNotNull(bestsellersIds);
    assertEquals(productBestsellersDtos.size(), bestsellersIds.size());

    for (int i = 0; i < productBestsellersDtos.size(); i++) {
      assertEquals(productBestsellersDtos.get(i).productId(), bestsellersIds.get(i));
    }
  }

  @Test
  void getAllProductsUnsortedTest() {
    var pageable = Pageable.builder().page(0).size(10).sort(List.of()).build();
    var product = getProductWithImageLink();
    var expectedProducts = singletonList(product);
    var expectedPage = getPage(expectedProducts, 1L, 1, 0, 10);
    List<String> tags = Collections.emptyList();

    when(productRepository.findAllProductsWithDefaultSorting(LANGUAGE_UK, pageable, tags)).thenReturn(expectedPage);
    doNothing().when(setPercentageOfTotalOrdersUseCase).setPercentOfTotalOrders(anyList());
    var actualPage = getAllProductsUseCase.getAllProducts(LANGUAGE_UK, pageable, tags);

    assertEquals(expectedPage, actualPage);
    verify(productRepository).findAllProductsWithDefaultSorting(LANGUAGE_UK, pageable, tags);
    verify(setPercentageOfTotalOrdersUseCase).setPercentOfTotalOrders(expectedProducts);
    verify(getProductBestsellersUseCase, never()).getProductBestsellers(DAYS, AMOUNT_OF_MOST_SOLD_ITEMS);
  }

  @Test
  void getAllProductsReturnsEmptyListTest() {
    var pageable = Pageable.builder().page(0).size(10).sort(List.of("price,desc")).build();
    var expectedPage = getPage(List.<Product>of(), 0L, 0, 0, 10);
    List<String> tags = Collections.emptyList();
    var productBestsellersDtos = List.of(new ProductBestsellersDto(UUID.fromString("f3786b85-0d0e-4bff-92f8-6f9d0b3dc6f5"), 45.68));

    when(getProductBestsellersUseCase.getProductBestsellers(DAYS, AMOUNT_OF_MOST_SOLD_ITEMS)).thenReturn(productBestsellersDtos);
    when(productRepository.findAllProducts(eq(LANGUAGE_UK), eq(pageable), eq(tags), anyList())).thenReturn(expectedPage);
    doNothing().when(setPercentageOfTotalOrdersUseCase).setPercentOfTotalOrders(anyList());

    var actualPage = getAllProductsUseCase.getAllProducts(LANGUAGE_UK, pageable, tags);

    assertEquals(expectedPage, actualPage);
    verify(productRepository).findAllProducts(eq(LANGUAGE_UK), eq(pageable), eq(tags), bestsellersIdsCaptor.capture());
    verify(setPercentageOfTotalOrdersUseCase).setPercentOfTotalOrders(expectedPage.content());
    verify(getProductBestsellersUseCase).getProductBestsellers(DAYS, AMOUNT_OF_MOST_SOLD_ITEMS);

    final List<UUID> bestsellersIds = bestsellersIdsCaptor.getValue();
    assertNotNull(bestsellersIds);
    assertEquals(productBestsellersDtos.size(), bestsellersIds.size());

    for (int i = 0; i < productBestsellersDtos.size(); i++) {
      assertEquals(productBestsellersDtos.get(i).productId(), bestsellersIds.get(i));
    }
  }
}
