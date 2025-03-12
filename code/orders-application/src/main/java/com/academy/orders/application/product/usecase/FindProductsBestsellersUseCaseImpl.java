package com.academy.orders.application.product.usecase;

import com.academy.orders.domain.common.Page;
import com.academy.orders.domain.common.Pageable;
import com.academy.orders.domain.language.exception.LanguageNotFoundException;
import com.academy.orders.domain.language.repository.LanguageRepository;
import com.academy.orders.domain.product.entity.Product;
import com.academy.orders.domain.product.repository.ProductRepository;
import com.academy.orders.domain.product.usecase.FindProductsBestsellersUseCase;
import com.academy.orders.domain.product.usecase.SetPercentageOfTotalOrdersUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class FindProductsBestsellersUseCaseImpl implements FindProductsBestsellersUseCase {
  private final ProductRepository productRepository;

  private final LanguageRepository languageRepository;

  private final SetPercentageOfTotalOrdersUseCase setPercentageOfTotalOrdersUseCase;

  @Override
  public Page<Product> findProductsBestsellers(Pageable pageable, final String language) {
    languageRepository.findByCode(language).orElseThrow(() -> new LanguageNotFoundException(language));
    final LocalDateTime now = LocalDateTime.now();
    final LocalDateTime then = now.minusDays(30);

    final Page<Product> products = productRepository.findMostSoldProducts(pageable, language, then, now, pageable.size());

    setPercentageOfTotalOrdersUseCase.setPercentOfTotalOrders(products.content());
    return products;
  }
}
