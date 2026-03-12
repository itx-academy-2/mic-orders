package com.academy.orders.application.product.usecase;

import com.academy.orders.domain.common.Page;
import com.academy.orders.domain.common.Pageable;
import com.academy.orders.domain.product.dto.ProductManagementFilterDto;
import com.academy.orders.domain.product.entity.Product;
import com.academy.orders.domain.product.entity.ProductManagementView;
import com.academy.orders.domain.product.repository.ProductRepository;
import com.academy.orders.domain.product.usecase.GetManagerProductsUseCase;
import com.academy.orders.domain.product.usecase.SetPercentageOfTotalOrdersUseCase;
import com.academy.orders.domain.reservation.repository.ReservationsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetManagerProductsUseCaseImpl implements GetManagerProductsUseCase {
  private final ProductRepository productRepository;

  private final ReservationsRepository reservationsRepository;

  private final SetPercentageOfTotalOrdersUseCase setPercentageOfTotalOrdersUseCase;

  @Override
  public Page<ProductManagementView> getManagerProducts(Pageable pageable, ProductManagementFilterDto filter, String lang) {
    Page<Product> page = productRepository.findAllByLanguageWithFilter(lang, filter, pageable);
    List<Product> products = page.content();
    List<UUID> productIds = products.stream()
        .map(Product::getId)
        .toList();

    Map<UUID, Long> reservedMap = reservationsRepository.getReservedQuantitiesByProductIds(productIds);

    setPercentageOfTotalOrdersUseCase.setPercentOfTotalOrders(products);

    return page.map(product -> toView(product, reservedMap));
  }

  private ProductManagementView toView(Product product, Map<UUID, Long> reservedMap) {
    return ProductManagementView.builder()
        .product(product)
        .reservedQuantity(reservedMap.getOrDefault(product.getId(), 0L).intValue())
        .build();
  }
}
