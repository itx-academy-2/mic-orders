package com.academy.orders.application.product.usecase;

import com.academy.orders.domain.product.dto.ProductBestsellersDto;
import com.academy.orders.domain.product.repository.ProductRepository;
import com.academy.orders.domain.product.usecase.GetProductBestsellersUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetProductBestsellersDtoUseCaseImpl implements GetProductBestsellersUseCase {
  private final ProductRepository productRepository;

  @Override
  @Cacheable(value = "bestsellers", key = "#days-#quantity")
  public List<ProductBestsellersDto> getProductBestsellers(final int days, final int quantity) {
    final LocalDateTime now = LocalDateTime.now();
    return productRepository.getIdsOfMostSoldProducts(now.minusDays(days), now, quantity);
  }

  @Scheduled(fixedRate = 1L, timeUnit = TimeUnit.HOURS)
  @CacheEvict(value = "bestsellers", allEntries = true)
  public void evictCache() {
    log.info("Cache evicted for bestsellers at {}", LocalDateTime.now());
  }
}
