package com.academy.orders.application.viewhistory.usecase;

import com.academy.orders.domain.product.exception.ProductNotFoundException;
import com.academy.orders.domain.product.repository.ProductRepository;
import com.academy.orders.domain.viewhistory.repository.ViewedHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AddProductToViewedHistoryUseCaseImpl
    implements com.academy.orders.domain.viewhistory.usecase.AddProductToViewedHistoryUseCase {

  private final ProductRepository productRepository;

  private final ViewedHistoryRepository viewedHistoryRepository;

  @Override
  public void addProductToViewedHistory(Long accountId, UUID productId) {
    log.debug("Checking existence of product {} for user {}", productId, accountId);
    if (!productRepository.existById(productId)) {
      log.warn("Product {} not found for user {}", productId, accountId);
      throw new ProductNotFoundException(productId);
    }
    log.info("User {} is adding product {} to viewed history", accountId, productId);
    viewedHistoryRepository.addOrUpdateViewedProduct(accountId, productId);
  }
}
