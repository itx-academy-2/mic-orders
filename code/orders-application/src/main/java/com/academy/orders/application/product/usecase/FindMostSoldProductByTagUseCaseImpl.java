package com.academy.orders.application.product.usecase;

import com.academy.orders.domain.common.Pageable;
import com.academy.orders.domain.product.entity.Product;
import com.academy.orders.domain.product.repository.ProductRepository;
import com.academy.orders.domain.product.usecase.FindMostSoldProductByTagUseCase;
import com.academy.orders.domain.product.usecase.SetPercentageOfTotalOrdersUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.academy.orders.domain.filter.FilterMetricsConstants.DAYS;

@Service
@RequiredArgsConstructor
public class FindMostSoldProductByTagUseCaseImpl implements FindMostSoldProductByTagUseCase {
  private final ProductRepository productRepository;

  private final SetPercentageOfTotalOrdersUseCase setPercentageOfTotalOrdersUseCase;

  @Override
  public Optional<Product> findMostSoldProductByTag(String lang, String tag) {
    final LocalDateTime now = LocalDateTime.now();
    final Pageable pageable = Pageable.builder().page(0).size(1).build();
    final List<Product> products =
        productRepository.findMostSoldProductsByTagAndPeriod(pageable, lang, now.minusDays(DAYS), now, tag).content();
    if (Objects.isNull(products) || products.isEmpty()) {
      return Optional.empty();
    }
    final Product product = products.get(0);

    setPercentageOfTotalOrdersUseCase.setPercentOfTotalOrders(product);
    return Optional.of(product);
  }
}
