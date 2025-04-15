package com.academy.orders.application.product.usecase;

import com.academy.orders.application.ModelUtils;
import com.academy.orders.application.TestConstants;
import com.academy.orders.domain.common.Page;
import com.academy.orders.domain.common.Pageable;
import com.academy.orders.domain.product.entity.Product;
import com.academy.orders.domain.product.exception.ProductNotFoundException;
import com.academy.orders.domain.product.repository.ProductRepository;
import com.academy.orders.domain.product.usecase.SetPercentageOfTotalOrdersUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;

import static com.academy.orders.application.ModelUtils.*;
import static com.academy.orders.application.TestConstants.LANGUAGE_EN;
import static com.academy.orders.domain.filter.FilterMetricsConstants.DAYS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FindMostSoldProductByTagUseCaseTest {
  @InjectMocks
  private FindMostSoldProductByTagUseCaseImpl findMostSoldProductByTagUseCase;

  @Mock
  private ProductRepository productRepository;

  @Mock
  private SetPercentageOfTotalOrdersUseCase setPercentageOfTotalOrdersUseCase;

  @Captor
  private ArgumentCaptor<LocalDateTime> startDateCaptor;

  @Captor
  private ArgumentCaptor<LocalDateTime> endDateCaptor;

  @Test
  void findMostSoldProductByTagWhenPageIsEmptyTest() {
    final String lang = LANGUAGE_EN;
    final String tag = "tag";
    final Page<Product> page = getPage(Collections.emptyList(), 0, 0, 0, 0);

    when(productRepository.findMostSoldProductsByTagAndPeriod(any(Pageable.class), eq(lang), any(LocalDateTime.class),
        any(LocalDateTime.class), eq(tag)))
            .thenReturn(page);

    assertThrows(ProductNotFoundException.class, () -> findMostSoldProductByTagUseCase.findMostSoldProductByTag(lang, tag));

    verify(productRepository).findMostSoldProductsByTagAndPeriod(any(Pageable.class), eq(lang), startDateCaptor.capture(),
        endDateCaptor.capture(), eq(tag));
    assertEquals(DAYS, ChronoUnit.DAYS.between(startDateCaptor.getValue(), endDateCaptor.getValue()));
    verify(setPercentageOfTotalOrdersUseCase, never()).setPercentOfTotalOrders(any(Product.class));
  }

  @Test
  void findMostSoldProductByTagTest() {
    final String lang = LANGUAGE_EN;
    final String tag = "tag";
    final Product product = getProductWithImageName();
    final Page<Product> page = getPageOf(product);

    when(productRepository.findMostSoldProductsByTagAndPeriod(any(Pageable.class), eq(lang), any(LocalDateTime.class),
        any(LocalDateTime.class), eq(tag)))
            .thenReturn(page);
    doNothing().when(setPercentageOfTotalOrdersUseCase).setPercentOfTotalOrders(product);

    final Product result = findMostSoldProductByTagUseCase.findMostSoldProductByTag(lang, tag);

    assertEquals(product, result);

    verify(productRepository).findMostSoldProductsByTagAndPeriod(any(Pageable.class), eq(lang), startDateCaptor.capture(),
        endDateCaptor.capture(), eq(tag));
    verify(setPercentageOfTotalOrdersUseCase).setPercentOfTotalOrders(product);
    assertEquals(DAYS, ChronoUnit.DAYS.between(startDateCaptor.getValue(), endDateCaptor.getValue()));
  }
}
