package com.academy.orders.application.viewhistory.usecase;

import com.academy.orders.domain.viewhistory.repository.ViewedHistoryRepository;
import com.academy.orders.domain.viewhistory.usecase.DeleteAllViewedProductsUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeleteAllViewedProductsUseCaseImpl implements DeleteAllViewedProductsUseCase {

  private final ViewedHistoryRepository viewedHistoryRepository;

  @Override
  public void deleteAllViewedProducts(Long accountId) {
    log.info("Deleting all viewed products for accountId={}", accountId);
    viewedHistoryRepository.deleteAllViewedProducts(accountId);
  }
}
