package com.academy.orders.application.wishlist.usecase;

import com.academy.orders.domain.common.Pageable;
import com.academy.orders.domain.product.entity.Product;
import com.academy.orders.domain.wishlist.repository.WishlistRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.academy.orders.application.ModelUtils.getPageOf;
import static com.academy.orders.application.ModelUtils.getPageable;
import static com.academy.orders.application.TestConstants.LANGUAGE_EN;
import static com.academy.orders.application.TestConstants.TEST_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetUserWishlistUseCaseImplTest {
  private static final Long TEST_USER_ID = TEST_ID;

  private static final Pageable TEST_PAGEABLE = getPageable();

  @InjectMocks
  private GetUserWishlistUseCaseImpl getUserWishlistUseCase;

  @Mock
  private WishlistRepository wishlistRepository;

  @Test
  void getProductsInUserWishlistTest() {
    // Given
    var expectedPage = getPageOf(new Product());
    when(wishlistRepository.getWishlistProducts(TEST_USER_ID, TEST_PAGEABLE, LANGUAGE_EN)).thenReturn(expectedPage);

    // When
    var result = getUserWishlistUseCase.getProductsInUserWishlist(TEST_USER_ID, LANGUAGE_EN, TEST_PAGEABLE);

    // Then
    assertEquals(expectedPage, result);
    verify(wishlistRepository, times(1)).getWishlistProducts(TEST_USER_ID, TEST_PAGEABLE, LANGUAGE_EN);
  }
}
