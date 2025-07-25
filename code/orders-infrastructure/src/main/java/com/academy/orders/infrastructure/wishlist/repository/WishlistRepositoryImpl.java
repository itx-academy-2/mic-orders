package com.academy.orders.infrastructure.wishlist.repository;

import com.academy.orders.domain.common.Page;
import com.academy.orders.domain.common.Pageable;
import com.academy.orders.domain.product.entity.Product;
import com.academy.orders.domain.wishlist.repository.WishlistRepository;
import com.academy.orders.infrastructure.common.PageableMapper;
import com.academy.orders.infrastructure.product.ProductMapper;
import com.academy.orders.infrastructure.wishlist.entity.WishlistEntity;
import com.academy.orders.infrastructure.wishlist.entity.WishlistId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

import static com.academy.orders.infrastructure.wishlist.repository.WishlistProductTranslationJpaAdapter.remapSort;

@Repository
@RequiredArgsConstructor
public class WishlistRepositoryImpl implements WishlistRepository {

  private final WishlistJpaAdapter wishlistJpaAdapter;

  private final WishlistProductTranslationJpaAdapter wishlistProductTranslationJpaAdapter;

  private final PageableMapper pageableMapper;

  private final ProductMapper productMapper;

  @Override
  public void addProductToWishlist(Long accountId, UUID productId) {
    var id = new WishlistId(accountId, productId);
    if (wishlistJpaAdapter.existsById(id)) {
      return;
    }
    WishlistEntity entity = new WishlistEntity(accountId, productId);
    wishlistJpaAdapter.save(entity);
  }

  @Override
  public void removeProductFromWishlist(Long accountId, UUID productId) {
    wishlistJpaAdapter.deleteById(new WishlistId(accountId, productId));
  }

  @Override
  @Transactional(readOnly = true)
  public Page<Product> getWishlistProducts(Long accountId, Pageable pageable, String language) {
    var springPageable = remapSort(pageableMapper.fromDomain(pageable));

    var pageEntities = wishlistProductTranslationJpaAdapter.findWishlistProductTranslations(accountId, language, springPageable);

    List<Product> products = pageEntities.getContent().stream()
        .map(productMapper::fromEntity)
        .toList();

    return Page.<Product>builder()
        .totalElements(pageEntities.getTotalElements())
        .totalPages(pageEntities.getTotalPages())
        .number(pageEntities.getNumber())
        .size(pageEntities.getSize())
        .numberOfElements(pageEntities.getNumberOfElements())
        .first(pageEntities.isFirst())
        .last(pageEntities.isLast())
        .empty(pageEntities.isEmpty())
        .content(products)
        .build();
  }
}
