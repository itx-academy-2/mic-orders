package com.academy.orders.application.wishlist.usecase;

import com.academy.orders.domain.wishlist.repository.WishlistRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.UUID;

import static com.academy.orders.application.TestConstants.TEST_ID;
import static com.academy.orders.application.TestConstants.TEST_UUID;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RemoveFromWishlistUseCaseImplTest {
  private static final Long TEST_USER_ID = TEST_ID;

  private static final UUID TEST_PRODUCT_ID = TEST_UUID;

  @InjectMocks
  private RemoveFromWishlistUseCaseImpl removeFromWishlistUseCase;

  @Mock
  private WishlistRepository wishlistRepository;

  @Test
  void removeProductFromWishlistWhenProductExistsTest() {
    // Given
    doNothing().when(wishlistRepository).removeProductFromWishlist(TEST_USER_ID, TEST_PRODUCT_ID);

    // When
    removeFromWishlistUseCase.removeProductFromWishlist(TEST_USER_ID, TEST_PRODUCT_ID);

    // Then
    verify(wishlistRepository, times(1)).removeProductFromWishlist(TEST_USER_ID, TEST_PRODUCT_ID);
  }
}
