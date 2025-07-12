package com.academy.orders.application.viewhistory.usecase;

import com.academy.orders.domain.common.Page;
import com.academy.orders.domain.common.Pageable;
import com.academy.orders.domain.product.entity.Product;
import com.academy.orders.domain.viewhistory.repository.ViewedHistoryRepository;
import com.academy.orders.domain.viewhistory.usecase.GetViewedProductsUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetViewedProductsUseCaseImpl implements GetViewedProductsUseCase {

  private final ViewedHistoryRepository viewedHistoryRepository;

  @Override
  public Page<Product> getViewedProducts(Long accountId, Pageable pageable, String language) {
    pageable = applyDefaultSort(pageable);
    log.info("Fetching viewed products for accountId={}, page={}, size={}, sort={}, language={}",
        accountId, pageable.page(), pageable.size(), pageable.sort(), language);
    return viewedHistoryRepository.getViewedProducts(accountId, pageable, language);
  }

  private Pageable applyDefaultSort(Pageable pageable) {
    if (pageable.sort() == null || pageable.sort().isEmpty()) {
      return Pageable.builder()
          .page(pageable.page())
          .size(pageable.size())
          .sort(List.of("viewedAt,DESC")) // Default sort
          .build();
    }
    return pageable;
  }
}
