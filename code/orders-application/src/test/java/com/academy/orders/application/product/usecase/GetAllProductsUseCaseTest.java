package com.academy.orders.application.product.usecase;

import com.academy.orders.domain.common.Pageable;
import com.academy.orders.domain.product.dto.PriceRangeDto;
import com.academy.orders.domain.product.dto.ProductBestsellersDto;
import com.academy.orders.domain.product.dto.ProductFilterDto;
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
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static com.academy.orders.application.ModelUtils.getPage;
import static com.academy.orders.application.ModelUtils.getProductWithImageLink;
import static com.academy.orders.application.ModelUtils.getProductFilterDto;
import static com.academy.orders.application.TestConstants.LANGUAGE_UK;
import static com.academy.orders.domain.filter.FilterMetricsConstants.AMOUNT_OF_MOST_SOLD_ITEMS;
import static com.academy.orders.domain.filter.FilterMetricsConstants.DAYS;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
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
    // Given
    var pageable = Pageable.builder().page(0).size(10).sort(List.of("name,desc")).build();
    var product = getProductWithImageLink();
    var expectedProducts = singletonList(product);
    var page = getPage(expectedProducts, 1L, 1, 0, 10);
    var productFilterDto = getProductFilterDto();
    var productBestsellersDtos = List.of(new ProductBestsellersDto(UUID.fromString("f3786b85-0d0e-4bff-92f8-6f9d0b3dc6f5"), 45.68));
    when(getProductBestsellersUseCase.getProductBestsellers(DAYS, AMOUNT_OF_MOST_SOLD_ITEMS)).thenReturn(productBestsellersDtos);
    when(productRepository.findAllProducts(eq(LANGUAGE_UK), eq(pageable), eq(productFilterDto), anyList())).thenReturn(page);
    when(productRepository.findMinMaxVisibleProductPrice())
        .thenReturn(new PriceRangeDto(BigDecimal.valueOf(100), BigDecimal.valueOf(1000)));
    doNothing().when(setPercentageOfTotalOrdersUseCase).setPercentOfTotalOrders(anyList());

    // When
    var actualPage = getAllProductsUseCase.getAllProducts(LANGUAGE_UK, pageable, productFilterDto);

    // Then
    assertFalse(actualPage.content().isEmpty());
    verify(productRepository).findAllProducts(eq(LANGUAGE_UK), eq(pageable), eq(productFilterDto), bestsellersIdsCaptor.capture());
    verify(productRepository).findMinMaxVisibleProductPrice();
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
    // Given
    var pageable = Pageable.builder().page(0).size(10).sort(List.of()).build();
    var product = getProductWithImageLink();
    var expectedProducts = singletonList(product);
    var page = getPage(expectedProducts, 1L, 1, 0, 10);
    var productFilterDto = getProductFilterDto();
    var productBestsellersDtos = List.of(new ProductBestsellersDto(UUID.fromString("f3786b85-0d0e-4bff-92f8-6f9d0b3dc6f5"), 45.68));
    when(getProductBestsellersUseCase.getProductBestsellers(DAYS, AMOUNT_OF_MOST_SOLD_ITEMS)).thenReturn(productBestsellersDtos);
    when(productRepository.findAllProducts(eq(LANGUAGE_UK), eq(pageable), eq(productFilterDto), anyList())).thenReturn(page);
    when(productRepository.findMinMaxVisibleProductPrice())
        .thenReturn(new PriceRangeDto(BigDecimal.valueOf(100), BigDecimal.valueOf(1000)));
    doNothing().when(setPercentageOfTotalOrdersUseCase).setPercentOfTotalOrders(anyList());

    // When
    var actualPage = getAllProductsUseCase.getAllProducts(LANGUAGE_UK, pageable, productFilterDto);

    // Then
    assertFalse(actualPage.content().isEmpty());
    verify(productRepository).findAllProducts(eq(LANGUAGE_UK), eq(pageable), eq(productFilterDto), anyList());
    verify(productRepository).findMinMaxVisibleProductPrice();
    verify(setPercentageOfTotalOrdersUseCase).setPercentOfTotalOrders(expectedProducts);
    verify(getProductBestsellersUseCase).getProductBestsellers(DAYS, AMOUNT_OF_MOST_SOLD_ITEMS);
  }

  @Test
  void getAllProductsReturnsEmptyListTest() {
    // Given
    var pageable = Pageable.builder().page(0).size(10).sort(List.of("price,desc")).build();
    var page = getPage(List.<Product>of(), 0L, 0, 0, 10);
    var productFilterDto = getProductFilterDto();
    var productBestsellersDtos = List.of(new ProductBestsellersDto(
        UUID.fromString("f3786b85-0d0e-4bff-92f8-6f9d0b3dc6f5"), 45.68));
    when(getProductBestsellersUseCase.getProductBestsellers(DAYS, AMOUNT_OF_MOST_SOLD_ITEMS)).thenReturn(productBestsellersDtos);
    when(productRepository.findAllProducts(eq(LANGUAGE_UK), eq(pageable), eq(productFilterDto), anyList())).thenReturn(page);
    when(productRepository.findMinMaxVisibleProductPrice())
        .thenReturn(new PriceRangeDto(BigDecimal.valueOf(100), BigDecimal.valueOf(1000)));
    doNothing().when(setPercentageOfTotalOrdersUseCase).setPercentOfTotalOrders(anyList());

    // When
    var actualPage = getAllProductsUseCase.getAllProducts(LANGUAGE_UK, pageable, productFilterDto);

    // Then
    assertTrue(actualPage.content().isEmpty());
    verify(productRepository).findAllProducts(eq(LANGUAGE_UK), eq(pageable), eq(productFilterDto), bestsellersIdsCaptor.capture());
    verify(productRepository).findMinMaxVisibleProductPrice();
    verify(setPercentageOfTotalOrdersUseCase).setPercentOfTotalOrders(page.content());
    verify(getProductBestsellersUseCase).getProductBestsellers(DAYS, AMOUNT_OF_MOST_SOLD_ITEMS);

    final List<UUID> bestsellersIds = bestsellersIdsCaptor.getValue();
    assertNotNull(bestsellersIds);
    assertEquals(productBestsellersDtos.size(), bestsellersIds.size());

    for (int i = 0; i < productBestsellersDtos.size(); i++) {
      assertEquals(productBestsellersDtos.get(i).productId(), bestsellersIds.get(i));
    }
  }

  @Test
  void validatePriceRangeThrowsWhenMinGreaterThanMaxTest() {
    // Given
    var pageable = Pageable.builder().page(0).size(10).sort(List.of()).build();
    var filter = com.academy.orders.domain.product.dto.ProductFilterDto.builder()
        .priceMin(java.math.BigDecimal.valueOf(100))
        .priceMax(java.math.BigDecimal.valueOf(10))
        .build();

    // When
    assertThrows(IllegalArgumentException.class, () -> getAllProductsUseCase.getAllProducts(LANGUAGE_UK, pageable, filter));

    // Then
    verifyNoInteractions(getProductBestsellersUseCase, productRepository, setPercentageOfTotalOrdersUseCase);
  }

  @Test
  void validatePriceRangeWhenFilterIsNullTest() {
    // Given
    var pageable = Pageable.builder().page(0).size(10).sort(List.of("price,desc")).build();
    var page = getPage(List.<Product>of(), 0L, 0, 0, 10);
    var productBestsellersDtos = List.of(new ProductBestsellersDto(
        UUID.fromString("f3786b85-0d0e-4bff-92f8-6f9d0b3dc6f5"), 45.68));
    when(getProductBestsellersUseCase.getProductBestsellers(DAYS, AMOUNT_OF_MOST_SOLD_ITEMS)).thenReturn(productBestsellersDtos);
    when(productRepository.findAllProducts(eq(LANGUAGE_UK), eq(pageable), eq(null), anyList())).thenReturn(page);
    when(productRepository.findMinMaxVisibleProductPrice())
        .thenReturn(new PriceRangeDto(BigDecimal.valueOf(100), BigDecimal.valueOf(1000)));
    doNothing().when(setPercentageOfTotalOrdersUseCase).setPercentOfTotalOrders(anyList());

    // When
    var actualPage = getAllProductsUseCase.getAllProducts(LANGUAGE_UK, pageable, null);

    // Then
    assertTrue(actualPage.content().isEmpty());
    verify(productRepository).findAllProducts(eq(LANGUAGE_UK), eq(pageable), eq(null), bestsellersIdsCaptor.capture());
    verify(productRepository).findMinMaxVisibleProductPrice();
    verify(setPercentageOfTotalOrdersUseCase).setPercentOfTotalOrders(page.content());
    verify(getProductBestsellersUseCase).getProductBestsellers(DAYS, AMOUNT_OF_MOST_SOLD_ITEMS);
  }

  @Test
  void validatePriceRangeWhenMinPricesIsNullTest() {
    // Given
    var pageable = Pageable.builder().page(0).size(10).sort(List.of("price,desc")).build();
    var page = getPage(List.<Product>of(), 0L, 0, 0, 10);
    var productBestsellersDtos = List.of(new ProductBestsellersDto(
        UUID.fromString("f3786b85-0d0e-4bff-92f8-6f9d0b3dc6f5"), 45.68));
    var productFilterDto = ProductFilterDto.builder()
        .priceMin(null)
        .priceMax(BigDecimal.valueOf(1000))
        .build();
    when(getProductBestsellersUseCase.getProductBestsellers(DAYS, AMOUNT_OF_MOST_SOLD_ITEMS)).thenReturn(productBestsellersDtos);
    when(productRepository.findAllProducts(eq(LANGUAGE_UK), eq(pageable), eq(productFilterDto), anyList())).thenReturn(page);
    when(productRepository.findMinMaxVisibleProductPrice())
        .thenReturn(new PriceRangeDto(BigDecimal.valueOf(100), BigDecimal.valueOf(1000)));
    doNothing().when(setPercentageOfTotalOrdersUseCase).setPercentOfTotalOrders(anyList());

    // When
    var actualPage = getAllProductsUseCase.getAllProducts(LANGUAGE_UK, pageable, productFilterDto);

    // Then
    assertTrue(actualPage.content().isEmpty());
    verify(productRepository).findAllProducts(eq(LANGUAGE_UK), eq(pageable), eq(productFilterDto), bestsellersIdsCaptor.capture());
    verify(productRepository).findMinMaxVisibleProductPrice();
    verify(setPercentageOfTotalOrdersUseCase).setPercentOfTotalOrders(page.content());
    verify(getProductBestsellersUseCase).getProductBestsellers(DAYS, AMOUNT_OF_MOST_SOLD_ITEMS);
  }

  @Test
  void validatePriceRangeWhenMaxPricesIsNullTest() {
    // Given
    var pageable = Pageable.builder().page(0).size(10).sort(List.of("price,desc")).build();
    var page = getPage(List.<Product>of(), 0L, 0, 0, 10);
    var productBestsellersDtos = List.of(new ProductBestsellersDto(
        UUID.fromString("f3786b85-0d0e-4bff-92f8-6f9d0b3dc6f5"), 45.68));
    var productFilterDto = ProductFilterDto.builder()
        .priceMin(BigDecimal.valueOf(100))
        .priceMax(null)
        .build();
    when(getProductBestsellersUseCase.getProductBestsellers(DAYS, AMOUNT_OF_MOST_SOLD_ITEMS)).thenReturn(productBestsellersDtos);
    when(productRepository.findAllProducts(eq(LANGUAGE_UK), eq(pageable), eq(productFilterDto), anyList())).thenReturn(page);
    when(productRepository.findMinMaxVisibleProductPrice())
        .thenReturn(new PriceRangeDto(BigDecimal.valueOf(100), BigDecimal.valueOf(1000)));
    doNothing().when(setPercentageOfTotalOrdersUseCase).setPercentOfTotalOrders(anyList());

    // When
    var actualPage = getAllProductsUseCase.getAllProducts(LANGUAGE_UK, pageable, productFilterDto);

    // Then
    assertTrue(actualPage.content().isEmpty());
    verify(productRepository).findAllProducts(eq(LANGUAGE_UK), eq(pageable), eq(productFilterDto), bestsellersIdsCaptor.capture());
    verify(productRepository).findMinMaxVisibleProductPrice();
    verify(setPercentageOfTotalOrdersUseCase).setPercentOfTotalOrders(page.content());
    verify(getProductBestsellersUseCase).getProductBestsellers(DAYS, AMOUNT_OF_MOST_SOLD_ITEMS);
  }
}
