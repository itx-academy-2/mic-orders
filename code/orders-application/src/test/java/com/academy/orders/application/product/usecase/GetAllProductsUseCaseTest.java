package com.academy.orders.application.product.usecase;

import com.academy.orders.domain.common.Pageable;
import com.academy.orders.domain.product.entity.Product;
import com.academy.orders.domain.product.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static com.academy.orders.application.ModelUtils.getPage;
import static com.academy.orders.application.ModelUtils.getProductWithImageLink;
import static com.academy.orders.application.TestConstants.LANGUAGE_UK;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetAllProductsUseCaseTest {
  @Mock
  private ProductRepository productRepository;

  @Mock
  private SetPercentageOfTotalOrdersUseCaseImpl setPercentageOfTotalOrdersUseCase;

  @InjectMocks
  private GetAllProductsUseCaseImpl getAllProductsUseCase;

  @Test
  void getAllProductsTest() {
    var pageable = Pageable.builder().page(0).size(10).sort(List.of("name,desc")).build();
    var product = getProductWithImageLink();
    var expectedProducts = singletonList(product);
    var expectedPage = getPage(expectedProducts, 1L, 1, 0, 10);
    List<String> tags = Collections.emptyList();

    when(productRepository.findAllProducts(LANGUAGE_UK, pageable, tags)).thenReturn(expectedPage);
    doNothing().when(setPercentageOfTotalOrdersUseCase).setPercentOfTotalOrders(anyList());

    var actualPage = getAllProductsUseCase.getAllProducts(LANGUAGE_UK, pageable, tags);

    assertEquals(expectedPage, actualPage);
    verify(productRepository).findAllProducts(LANGUAGE_UK, pageable, tags);
    verify(setPercentageOfTotalOrdersUseCase).setPercentOfTotalOrders(anyList());
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
    verify(setPercentageOfTotalOrdersUseCase).setPercentOfTotalOrders(anyList());
  }

  @Test
  void getAllProductsReturnsEmptyListTest() {
    var pageable = Pageable.builder().page(0).size(10).sort(List.of("price,desc")).build();
    var expectedPage = getPage(List.<Product>of(), 0L, 0, 0, 10);
    List<String> tags = Collections.emptyList();

    when(productRepository.findAllProducts(LANGUAGE_UK, pageable, tags)).thenReturn(expectedPage);
    doNothing().when(setPercentageOfTotalOrdersUseCase).setPercentOfTotalOrders(anyList());
    var actualPage = getAllProductsUseCase.getAllProducts(LANGUAGE_UK, pageable, tags);

    assertEquals(expectedPage, actualPage);
    verify(productRepository).findAllProducts(LANGUAGE_UK, pageable, tags);
    verify(setPercentageOfTotalOrdersUseCase).setPercentOfTotalOrders(anyList());
  }
}
