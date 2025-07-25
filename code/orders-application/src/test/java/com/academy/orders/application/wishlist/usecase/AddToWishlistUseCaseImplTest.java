package com.academy.orders.application.wishlist.usecase;

import com.academy.orders.domain.product.exception.ProductNotFoundException;
import com.academy.orders.domain.product.repository.ProductRepository;
import com.academy.orders.domain.wishlist.repository.WishlistRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.UUID;

import static com.academy.orders.application.TestConstants.TEST_ID;
import static com.academy.orders.application.TestConstants.TEST_UUID;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AddToWishlistUseCaseImplTest {
  private static final Long TEST_USER_ID = TEST_ID;

  private static final UUID TEST_PRODUCT_ID = TEST_UUID;

  @InjectMocks
  private AddToWishlistUseCaseImpl addToWishlistUseCase;

  @Mock
  private ProductRepository productRepository;

  @Mock
  private WishlistRepository wishlistRepository;

  @Test
  void addProductToWishlistWhenProductExistsTest() {
    // Given
    when(productRepository.existById(TEST_PRODUCT_ID)).thenReturn(true);
    doNothing().when(wishlistRepository).addProductToWishlist(TEST_USER_ID, TEST_PRODUCT_ID);

    // When
    addToWishlistUseCase.addProductToWishlist(TEST_USER_ID, TEST_PRODUCT_ID);

    // Then
    verify(productRepository, times(1)).existById(TEST_PRODUCT_ID);
    verify(wishlistRepository, times(1)).addProductToWishlist(TEST_USER_ID, TEST_PRODUCT_ID);
  }

  @Test
  void addProductToWishlistWhenProductDoesNotExistTest() {
    // Given
    when(productRepository.existById(TEST_PRODUCT_ID)).thenReturn(false);

    // When / Then
    assertThrows(ProductNotFoundException.class, () -> addToWishlistUseCase.addProductToWishlist(TEST_USER_ID, TEST_PRODUCT_ID));

    verify(productRepository, times(1)).existById(TEST_PRODUCT_ID);
    verify(wishlistRepository, never()).addProductToWishlist(any(), any());
  }
}
