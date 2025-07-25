package com.academy.orders.application.wishlist.usecase;

import com.academy.orders.domain.common.Page;
import com.academy.orders.domain.common.Pageable;
import com.academy.orders.domain.product.entity.Product;
import com.academy.orders.domain.wishlist.repository.WishlistRepository;
import com.academy.orders.domain.wishlist.usecase.GetUserWishlistUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetUserWishlistUseCaseImpl implements GetUserWishlistUseCase {

  private final WishlistRepository wishlistRepository;

  @Override
  public Page<Product> getProductsInUserWishlist(Long userId, String language, Pageable pageable) {
    log.info("Fetching wishlist products for user {} in language {}", userId, language);
    return wishlistRepository.getWishlistProducts(userId, pageable, language);
  }
}
