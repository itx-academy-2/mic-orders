package com.academy.orders.application.product.usecase;

import com.academy.orders.domain.common.Page;
import com.academy.orders.domain.common.Pageable;
import com.academy.orders.domain.product.dto.PageProductsDto;
import com.academy.orders.domain.product.dto.PriceRangeDto;
import com.academy.orders.domain.product.dto.ProductBestsellersDto;
import com.academy.orders.domain.product.dto.ProductFilterDto;
import com.academy.orders.domain.product.entity.Product;
import com.academy.orders.domain.product.repository.ProductRepository;
import com.academy.orders.domain.product.usecase.GetAllProductsUseCase;
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
public class GetAllProductsUseCaseImpl implements GetAllProductsUseCase {
  private final ProductRepository productRepository;

  private final GetProductBestsellersUseCase getProductBestsellersUseCase;

  private final SetPercentageOfTotalOrdersUseCase setPercentageOfTotalOrdersUseCase;

  @Override
  public PageProductsDto<Product> getAllProducts(String language, Pageable pageable, ProductFilterDto filter) {
    validatePriceRange(filter);

    List<UUID> bestsellersId = getBestsellersIds();

    Page<Product> products = productRepository.findAllProducts(language, pageable, filter, bestsellersId);
    PriceRangeDto priceRange = productRepository.findMinMaxVisibleProductPrice();
    setPercentageOfTotalOrdersUseCase.setPercentOfTotalOrders(products.content());

    return new PageProductsDto<Product>(
        products.totalElements(),
        products.totalPages(),
        products.first(),
        products.last(),
        products.number(),
        products.numberOfElements(),
        products.size(),
        products.empty(),
        priceRange.minPrice(),
        priceRange.maxPrice(),
        products.content());
  }

  private void validatePriceRange(ProductFilterDto filter) {
    if (filter == null) {
      return;
    }
    if (filter.priceMin() != null && filter.priceMax() != null) {
      if (filter.priceMin().compareTo(filter.priceMax()) > 0) {
        throw new IllegalArgumentException("priceMin must be less than or equal to priceMax");
      }
    }
  }

  private List<UUID> getBestsellersIds() {
    return getProductBestsellersUseCase
        .getProductBestsellers(DAYS, AMOUNT_OF_MOST_SOLD_ITEMS)
        .stream()
        .map(ProductBestsellersDto::productId)
        .toList();
  }
}
