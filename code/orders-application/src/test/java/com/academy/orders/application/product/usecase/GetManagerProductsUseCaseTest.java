package com.academy.orders.application.product.usecase;

import com.academy.orders.domain.product.repository.ProductRepository;
import com.academy.orders.domain.product.usecase.SetPercentageOfTotalOrdersUseCase;
import com.academy.orders.domain.reservation.repository.ReservationsRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import java.util.Map;

import static com.academy.orders.application.ModelUtils.getManagementFilterDto;
import static com.academy.orders.application.ModelUtils.getPageOf;
import static com.academy.orders.application.ModelUtils.getPageable;
import static com.academy.orders.application.ModelUtils.getProductWithImageLink;
import static com.academy.orders.application.TestConstants.LANGUAGE_UK;
import static org.junit.jupiter.api.Assertions.assertEquals;
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

  @Mock
  private ReservationsRepository reservationsRepository;

  @Test
  void getManagerProductsTest() {
    // Given
    var product = getProductWithImageLink();
    var filter = getManagementFilterDto();
    var pageable = getPageable();
    var page = getPageOf(product);

    when(productRepository.findAllByLanguageWithFilter(LANGUAGE_UK, filter, pageable)).thenReturn(page);
    when(reservationsRepository.getReservedQuantitiesByProductIds(List.of(product.getId()))).thenReturn(Map.of(product.getId(), 1L));

    // When
    var actual = getManagerProductsUseCase.getManagerProducts(pageable, filter, LANGUAGE_UK);

    // Then
    assertEquals(1, actual.content().size());
    assertEquals(product.getId(), actual.content().get(0).getProduct().getId());
    assertEquals(1, actual.content().get(0).getReservedQuantity());

    verify(productRepository).findAllByLanguageWithFilter(LANGUAGE_UK, filter, pageable);
    verify(reservationsRepository).getReservedQuantitiesByProductIds(List.of(product.getId()));
    verify(setPercentageOfTotalOrdersUseCase).setPercentOfTotalOrders(page.content());
  }
}
