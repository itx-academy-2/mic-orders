package com.academy.orders.infrastructure.viewhistory.repository;

import com.academy.orders.domain.common.Page;
import com.academy.orders.domain.common.Pageable;
import com.academy.orders.domain.product.entity.Product;
import com.academy.orders.domain.viewhistory.repository.ViewedHistoryRepository;
import com.academy.orders.infrastructure.common.PageableMapper;
import com.academy.orders.infrastructure.product.ProductMapper;
import com.academy.orders.infrastructure.product.entity.ProductEntity;
import com.academy.orders.infrastructure.product.repository.ProductJpaAdapter;
import com.academy.orders.infrastructure.viewhistory.entity.ViewedHistoryEntity;
import com.academy.orders.infrastructure.viewhistory.entity.ViewedHistoryId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ViewedHistoryRepositoryImpl implements ViewedHistoryRepository {

  private final ViewedHistoryJpaAdapter viewedHistoryJpaAdapter;

  private final ProductJpaAdapter productJpaAdapter;

  private final PageableMapper pageableMapper;

  private final ProductMapper productMapper;

  @Override
  public void addOrUpdateViewedProduct(Long accountId, UUID productId) {
    var id = new ViewedHistoryId(accountId, productId);
    ViewedHistoryEntity entity = viewedHistoryJpaAdapter.findById(id)
        .orElse(new ViewedHistoryEntity(accountId, productId));
    entity.updateViewedAt();
    viewedHistoryJpaAdapter.save(entity);
  }

  @Override
  public void deleteAllViewedProducts(Long accountId) {
    viewedHistoryJpaAdapter.deleteAllByAccountId(accountId);
  }

  @Override
  public void deleteViewedProduct(Long accountId, UUID productId) {
    viewedHistoryJpaAdapter.deleteById(new ViewedHistoryId(accountId, productId));
  }

  @Override
  public Page<Product> getViewedProducts(Long accountId, Pageable pageable, String language) {
    var viewedHistoryPage = viewedHistoryJpaAdapter.findAllByIdAccountId(accountId, pageableMapper.fromDomain(pageable));
    List<UUID> productIds = extractProductIds(viewedHistoryPage);

    if (productIds.isEmpty()) {
      return emptyProductPage(pageable);
    }

    List<Product> products = fetchAndSortProductsByViewedOrder(productIds, language);

    return Page.<Product>builder()
        .totalElements(viewedHistoryPage.getTotalElements())
        .totalPages(viewedHistoryPage.getTotalPages())
        .first(viewedHistoryPage.isFirst())
        .last(viewedHistoryPage.isLast())
        .number(viewedHistoryPage.getNumber())
        .numberOfElements(viewedHistoryPage.getNumberOfElements())
        .size(viewedHistoryPage.getSize())
        .empty(viewedHistoryPage.isEmpty())
        .content(products)
        .build();
  }

  private List<UUID> extractProductIds(org.springframework.data.domain.Page<ViewedHistoryEntity> viewedHistoryPage) {
    return viewedHistoryPage.getContent().stream()
        .map(entity -> entity.getId().getProductId())
        .toList();
  }

  private List<Product> fetchAndSortProductsByViewedOrder(List<UUID> productIds, String language) {
    var productEntities = productJpaAdapter.findVisibleProductsByIdsAndLanguage(productIds, language);
    var productMap = productEntities.stream()
        .collect(Collectors.toMap(ProductEntity::getId, p -> p));
    return productIds.stream()
        .map(productMap::get)
        .map(productMapper::fromEntity)
        .toList();
  }

  private Page<Product> emptyProductPage(Pageable pageable) {
    return Page.<Product>builder()
        .totalElements(0L)
        .totalPages(0)
        .first(true)
        .last(true)
        .number(pageable.page())
        .numberOfElements(0)
        .size(pageable.size())
        .empty(true)
        .content(List.of())
        .build();
  }

}
