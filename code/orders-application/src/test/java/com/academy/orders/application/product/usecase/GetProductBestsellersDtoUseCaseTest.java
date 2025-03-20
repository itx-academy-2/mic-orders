package com.academy.orders.application.product.usecase;

import com.academy.orders.domain.product.dto.ProductBestsellersDto;
import com.academy.orders.domain.product.entity.Product;
import com.academy.orders.domain.product.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static com.academy.orders.application.ModelUtils.getProductWithImageLinkAndDiscount;
import static com.academy.orders.application.TestConstants.PERCENTAGE_OF_TOTAL_ORDERS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GetProductBestsellersDtoUseCaseTest {
  @Captor
  private ArgumentCaptor<LocalDateTime> startDateCaptor;

  @Captor
  private ArgumentCaptor<LocalDateTime> endDateCaptor;

  @Mock
  private ProductRepository productRepository;

  @InjectMocks
  private GetProductBestsellersDtoUseCaseImpl getProductBestsellersDtoUseCase;

  @Test
  void test() {
    final Product product = getProductWithImageLinkAndDiscount(30);
    final List<ProductBestsellersDto> productBestsellersDtos = List.of(
        new ProductBestsellersDto(product.getId(), PERCENTAGE_OF_TOTAL_ORDERS));
    final int days = 30;
    final int quantity = 1;

    when(productRepository.getIdsOfMostSoldProducts(any(), any(), eq(quantity)))
        .thenReturn(productBestsellersDtos);

    getProductBestsellersDtoUseCase.getProductBestsellers(days, quantity);

    verify(productRepository).getIdsOfMostSoldProducts(startDateCaptor.capture(), endDateCaptor.capture(), eq(quantity));
    assertEquals(days, ChronoUnit.DAYS.between(startDateCaptor.getValue(), endDateCaptor.getValue()));
  }
}
