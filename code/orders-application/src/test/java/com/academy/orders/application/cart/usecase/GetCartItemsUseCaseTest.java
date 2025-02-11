package com.academy.orders.application.cart.usecase;

import com.academy.orders.domain.cart.dto.CartItemDto;
import com.academy.orders.domain.cart.dto.CartResponseDto;
import com.academy.orders.domain.cart.entity.CartItem;
import com.academy.orders.domain.cart.repository.CartItemImageRepository;
import com.academy.orders.domain.cart.repository.CartItemRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static com.academy.orders.application.ModelUtils.*;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetCartItemsUseCaseTest {
  @InjectMocks
  private GetCartItemsUseCaseImpl getCartItemsUseCase;

  @Mock
  private CartItemRepository cartItemRepository;

  @Mock
  private CartItemImageRepository cartItemImageRepository;

  @Test
  void getCartItemsTest() {
    var accountId = 1L;
    var lang = "uk";
    var cartItem = getCartItem();
    var cartItemPrice = BigDecimal.valueOf(999.99);
    var totalPrice = BigDecimal.valueOf(999.99);
    final CartItemDto cartItemDto = getCartItemDto(cartItem, cartItemPrice);
    var cartResponseDto = getCartResponseDto(singletonList(cartItemDto), totalPrice, null);

    when(cartItemRepository.findCartItemsByAccountIdAndLang(anyLong(), anyString()))
        .thenReturn(singletonList(cartItem));
    when(cartItemImageRepository.loadImageForProductInCart(cartItem)).thenReturn(cartItem);

    final CartResponseDto result = getCartItemsUseCase.getCartItems(accountId, lang);

    assertEquals(cartResponseDto.totalPriceWithDiscount(), result.totalPriceWithDiscount());
    assertEquals(cartResponseDto.totalPrice(), result.totalPrice());
    assertCartItemEquals(cartItemDto, result.items().get(0));

    verify(cartItemRepository).findCartItemsByAccountIdAndLang(anyLong(), anyString());
    verify(cartItemImageRepository).loadImageForProductInCart(cartItem);
  }

  @Test
  void getCartItemsWithDiscountTest() {
    final long accountId = 1L;
    final String lang = "uk";
    final CartItem cartItem = getCartItemWithDiscount();
    final CartItemDto cartItemDto = getCartItemDtoWithDiscount(cartItem);
    final CartResponseDto cartResponseDto = getCartResponseDto(List.of(cartItemDto),
        cartItem.product().getPrice().multiply(BigDecimal.valueOf(cartItem.quantity())),
        cartItem.product().getPriceWithDiscount().multiply(BigDecimal.valueOf(cartItem.quantity())));

    when(cartItemRepository.findCartItemsByAccountIdAndLang(accountId, lang)).thenReturn(List.of(cartItem));
    when(cartItemImageRepository.loadImageForProductInCart(cartItem))
        .thenAnswer(a -> a.getArgument(0));

    final CartResponseDto result = getCartItemsUseCase.getCartItems(accountId, lang);

    assertEquals(cartResponseDto.totalPriceWithDiscount(), result.totalPriceWithDiscount());
    assertEquals(cartResponseDto.totalPrice(), result.totalPrice());
    assertCartItemEquals(cartItemDto, result.items().get(0));

    verify(cartItemRepository).findCartItemsByAccountIdAndLang(anyLong(), anyString());
    verify(cartItemImageRepository).loadImageForProductInCart(cartItem);
  }

  private void assertCartItemEquals(CartItemDto expected, CartItemDto actual) {
    assertEquals(expected.productId(), actual.productId());
    assertEquals(expected.image(), actual.image());
    assertEquals(expected.name(), actual.name());
    assertEquals(expected.productPrice(), actual.productPrice());
    assertEquals(expected.productPriceWithDiscount(), actual.productPriceWithDiscount());
    assertEquals(expected.discount(), actual.discount());
    assertEquals(expected.quantity(), actual.quantity());
    assertEquals(expected.calculatedPrice(), actual.calculatedPrice());
    assertEquals(expected.calculatedPriceWithDiscount(), actual.calculatedPriceWithDiscount());
  }

}
