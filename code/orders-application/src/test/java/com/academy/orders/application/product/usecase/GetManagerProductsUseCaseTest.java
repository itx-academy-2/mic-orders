package com.academy.orders.application.product.usecase;

import com.academy.orders.domain.product.repository.ProductRepository;
import com.academy.orders.domain.product.usecase.SetPercentageOfTotalOrdersUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.academy.orders.application.ModelUtils.getManagementFilterDto;
import static com.academy.orders.application.ModelUtils.getPageOf;
import static com.academy.orders.application.ModelUtils.getPageable;
import static com.academy.orders.application.ModelUtils.getProductWithImageLink;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetManagerProductsUseCaseTest {
  @InjectMocks
  private GetManagerProductsUseCaseImpl getManagerProductsUseCase;

  @Mock
  private SetPercentageOfTotalOrdersUseCase setPercentageOfTotalOrdersUseCase;

  @Mock
  private ProductRepository productRepository;

  @Test
  void getManagerProductsTest() {
    var product = getProductWithImageLink();
    var filter = getManagementFilterDto();
    var lang = "uk";
    var pageable = getPageable();
    var page = getPageOf(product);

    when(productRepository.findAllByLanguageWithFilter(lang, filter, pageable)).thenReturn(page);
    doNothing().when(setPercentageOfTotalOrdersUseCase).setPercentOfTotalOrders(anyList());
    var actual = getManagerProductsUseCase.getManagerProducts(pageable, filter, lang);

    assertEquals(page, actual);
    verify(productRepository).findAllByLanguageWithFilter(lang, filter, pageable);
    verify(setPercentageOfTotalOrdersUseCase).setPercentOfTotalOrders(page.content());
  }
}
