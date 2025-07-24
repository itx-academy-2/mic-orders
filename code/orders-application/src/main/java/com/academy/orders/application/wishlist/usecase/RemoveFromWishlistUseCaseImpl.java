package com.academy.orders.application.wishlist.usecase;

import com.academy.orders.domain.wishlist.repository.WishlistRepository;
import com.academy.orders.domain.wishlist.usecase.RemoveFromWishlistUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RemoveFromWishlistUseCaseImpl implements RemoveFromWishlistUseCase {

  private final WishlistRepository wishlistRepository;

  @Override
  public void removeProductFromWishlist(Long userId, UUID productId) {
    log.info("User {} is removing product {} from wishlist", userId, productId);
    wishlistRepository.removeProductFromWishlist(userId, productId);
  }
}
