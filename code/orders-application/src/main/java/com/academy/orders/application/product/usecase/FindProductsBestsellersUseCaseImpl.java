package com.academy.orders.application.product.usecase;

import com.academy.orders.domain.common.Page;
import com.academy.orders.domain.common.Pageable;
import com.academy.orders.domain.language.exception.LanguageNotFoundException;
import com.academy.orders.domain.language.repository.LanguageRepository;
import com.academy.orders.domain.product.dto.ProductBestsellersDto;
import com.academy.orders.domain.product.dto.ProductLanguageDto;
import com.academy.orders.domain.product.entity.Product;
import com.academy.orders.domain.product.repository.ProductRepository;
import com.academy.orders.domain.product.usecase.FindProductsBestsellersUseCase;
import com.academy.orders.domain.product.usecase.GetProductBestsellersUseCase;
import com.academy.orders.domain.product.usecase.SetPercentageOfTotalOrdersUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static com.academy.orders.domain.filter.FilterMetricsConstants.AMOUNT_OF_MOST_SOLD_ITEMS;
import static com.academy.orders.domain.filter.FilterMetricsConstants.DAYS;

@Service
@RequiredArgsConstructor
public class FindProductsBestsellersUseCaseImpl implements FindProductsBestsellersUseCase {
  private final ProductRepository productRepository;

  private final GetProductBestsellersUseCase getProductBestsellersUseCase;

  private final LanguageRepository languageRepository;

  private final SetPercentageOfTotalOrdersUseCase setPercentageOfTotalOrdersUseCase;

  @Override
  public Page<Product> findProductsBestsellers(Pageable pageable, final String language) {
    languageRepository.findByCode(language).orElseThrow(() -> new LanguageNotFoundException(language));

    final List<UUID> ids = getProductBestsellersUseCase.getProductBestsellers(DAYS, AMOUNT_OF_MOST_SOLD_ITEMS).stream()
        .map(ProductBestsellersDto::productId)
        .toList();
    final List<ProductLanguageDto> productLanguageDtos = productRepository.getProductLanguagesDto(language, ids).stream()
        .filter(o -> !o.code().equals(language))
        .toList();

    final Page<Product> products = productRepository.findProductsByLanguageAndIds(pageable, language, ids, productLanguageDtos);

    setPercentageOfTotalOrdersUseCase.setPercentOfTotalOrders(products.content());
    return products;
  }
}
