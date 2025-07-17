package com.academy.orders.application.viewhistory.usecase;

import com.academy.orders.domain.viewhistory.repository.ViewedHistoryRepository;
import com.academy.orders.domain.viewhistory.usecase.DeleteProductFromViewedHistoryUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeleteProductFromViewedHistoryUseCaseImpl implements DeleteProductFromViewedHistoryUseCase {

  private final ViewedHistoryRepository viewedHistoryRepository;

  @Override
  public void deleteProductFromViewedHistory(Long accountId, UUID productId) {
    log.info("Deleting viewed product with productId={} for accountId={}", productId, accountId);
    viewedHistoryRepository.deleteViewedProduct(accountId, productId);
  }
}
