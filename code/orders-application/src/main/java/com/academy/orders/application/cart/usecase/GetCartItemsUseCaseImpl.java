package com.academy.orders.application.cart.usecase;

import com.academy.orders.domain.cart.dto.CartItemDto;
import com.academy.orders.domain.cart.dto.CartResponseDto;
import com.academy.orders.domain.cart.entity.CartItem;
import com.academy.orders.domain.cart.repository.CartItemImageRepository;
import com.academy.orders.domain.cart.repository.CartItemRepository;
import com.academy.orders.domain.cart.usecase.CalculatePriceUseCase;
import com.academy.orders.domain.cart.usecase.GetCartItemsUseCase;
import com.academy.orders.domain.product.entity.ProductTranslation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class GetCartItemsUseCaseImpl implements GetCartItemsUseCase {
  private final CartItemRepository cartItemRepository;

  private final CalculatePriceUseCase calculatePriceUseCase;

  private final CartItemImageRepository cartItemImageRepository;

  @Override
  public CartResponseDto getCartItems(Long accountId, String lang) {
    var cartItems = getCartItemsByAccountIdAndLang(accountId, lang);
    var cartItemDtos = mapToCartItemsDtos(cartItems);
    final BigDecimal totalPrice = calculatePriceUseCase.calculateCartTotalPrice(cartItems);
    final BigDecimal totalPriceWithDiscount = calculatePriceUseCase.calculateTotalPriceWithDiscount(cartItems);
    return buildCartResponseDTO(cartItemDtos, totalPrice, totalPriceWithDiscount);
  }

  private List<CartItem> getCartItemsByAccountIdAndLang(Long accountId, String lang) {
    return cartItemRepository.findCartItemsByAccountIdAndLang(accountId, lang).stream()
        .map(cartItemImageRepository::loadImageForProductInCart).toList();
  }

  private List<CartItemDto> mapToCartItemsDtos(List<CartItem> cartItems) {
    return cartItems.stream().map(this::toCartItemDto).toList();
  }

  private CartItemDto toCartItemDto(CartItem cartItem) {
    var productItem = cartItem.product();

    return CartItemDto.builder()
        .productId(productItem.getId())
        .image(productItem.getImage())
        .name(mapName(productItem.getProductTranslations()))
        .productPrice(productItem.getPrice())
        .productPriceWithDiscount(productItem.getPriceWithDiscount())
        .discount(productItem.getDiscountAmount())
        .quantity(cartItem.quantity())
        .calculatedPrice(calculatePriceUseCase.calculateCartItemPrice(cartItem))
        .calculatedPriceWithDiscount(calculatePriceUseCase.calculateCartItemPriceWithDiscount(cartItem))
        .build();
  }

  private String mapName(Set<ProductTranslation> productTranslations) {
    return productTranslations.stream().iterator().next().name();
  }

  private CartResponseDto buildCartResponseDTO(
      List<CartItemDto> cartItemDtos,
      BigDecimal totalPrice,
      BigDecimal totalPriceWithDiscount) {
    return CartResponseDto.builder()
        .items(cartItemDtos)
        .totalPrice(totalPrice)
        .totalPriceWithDiscount(totalPriceWithDiscount)
        .build();
  }

}
