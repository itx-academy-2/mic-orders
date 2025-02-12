package com.academy.orders.application.product.usecase;

import com.academy.orders.application.ModelUtils;
import com.academy.orders.domain.common.Page;
import com.academy.orders.domain.common.Pageable;
import com.academy.orders.domain.product.entity.Product;
import com.academy.orders.domain.product.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetProductSearchResultsUseCaseTest {
  @Mock
  private ProductRepository productRepository;

  @InjectMocks
  private GetProductSearchResultsUseCaseImpl getProductSearchResultsUseCase;

  @Test
  void findProductsBySearchQueryTest() {
    // Given
    var pageable = ModelUtils.getPageable();
    String searchQuery = "some text";
    String lang = "en";
    Product product = ModelUtils.getProductWithImageLink();
    var productPage = ModelUtils.getPageOf(product);

    when(productRepository.searchProductsByName(searchQuery, lang, pageable)).thenReturn(productPage);

    // When
    Page<Product> products = getProductSearchResultsUseCase.findProductsBySearchQuery(searchQuery, lang, pageable);

    // Then
    assertEquals(productPage, products);
    verify(productRepository).searchProductsByName(searchQuery, lang, pageable);
  }

  @Test
  void findProductsBySearchQueryWithEmptySortTest() {
    // Given
    Pageable pageable = new Pageable(0, 8, List.of());
    Pageable defaultPageable = new Pageable(0, 8, List.of("name,desc"));
    String searchQuery = "some text";
    String lang = "en";
    Product product = ModelUtils.getProductWithImageLink();
    var productPage = ModelUtils.getPageOf(product);

    when(productRepository.searchProductsByName(searchQuery, lang, defaultPageable)).thenReturn(productPage);

    // When
    Page<Product> products = getProductSearchResultsUseCase.findProductsBySearchQuery(searchQuery, lang, pageable);

    // Then
    assertEquals(productPage, products);
    verify(productRepository).searchProductsByName(searchQuery, lang, defaultPageable);
  }
}
