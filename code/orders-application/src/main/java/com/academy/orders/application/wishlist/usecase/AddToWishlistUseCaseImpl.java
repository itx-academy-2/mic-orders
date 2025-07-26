package com.academy.orders.application.wishlist.usecase;

import com.academy.orders.domain.product.exception.ProductNotFoundException;
import com.academy.orders.domain.product.repository.ProductRepository;
import com.academy.orders.domain.wishlist.repository.WishlistRepository;
import com.academy.orders.domain.wishlist.usecase.AddToWishlistUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AddToWishlistUseCaseImpl implements AddToWishlistUseCase {

  private final ProductRepository productRepository;

  private final WishlistRepository wishlistRepository;

  @Override
  public void addProductToWishlist(Long userId, UUID productId) {
    log.debug("Checking existence of product {} for user {}", productId, userId);
    if (!productRepository.existById(productId)) {
      log.warn("Product {} not found for user {}", productId, userId);
      throw new ProductNotFoundException(productId);
    }
    log.info("User {} is adding product {} to wishlist", userId, productId);
    wishlistRepository.addProductToWishlist(userId, productId);
  }
}
