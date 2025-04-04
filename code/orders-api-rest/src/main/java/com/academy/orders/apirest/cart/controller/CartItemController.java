package com.academy.orders.apirest.cart.controller;

import com.academy.orders.apirest.cart.mapper.CartItemDTOMapper;
import com.academy.orders.domain.cart.entity.CreateCartItemDTO;
import com.academy.orders.domain.cart.usecase.CreateCartItemByUserUseCase;
import com.academy.orders.domain.cart.usecase.DeleteProductFromCartUseCase;
import com.academy.orders.domain.cart.usecase.GetCartItemsUseCase;
import com.academy.orders.domain.cart.usecase.UpdateCartItemQuantityUseCase;
import com.academy.orders_api_rest.generated.api.CartApi;
import com.academy.orders_api_rest.generated.model.CartItemsResponseDTO;
import com.academy.orders_api_rest.generated.model.UpdatedCartItemDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
public class CartItemController implements CartApi {
  private final CreateCartItemByUserUseCase cartItemByUserUseCase;

  private final DeleteProductFromCartUseCase deleteProductFromCartUseCase;

  private final GetCartItemsUseCase getCartItemsUseCase;

  private final CartItemDTOMapper cartItemDTOMapper;

  private final UpdateCartItemQuantityUseCase updateCartItemQuantityUseCase;

  @Override
  @PreAuthorize("hasAuthority('ROLE_ADMIN') || "
      + "(hasAuthority('ROLE_USER') && @checkAccountIdUseCaseImpl.hasSameId(#userId))")
  public void addProductToCart(UUID productId, Long userId) {
    cartItemByUserUseCase.create(new CreateCartItemDTO(productId, userId, 1));
  }

  @Override
  @PreAuthorize("hasAuthority('ROLE_ADMIN') || "
      + "(hasAuthority('ROLE_USER') && @checkAccountIdUseCaseImpl.hasSameId(#userId))")
  public void deleteProductFromCart(Long userId, UUID productId) {
    deleteProductFromCartUseCase.deleteProductFromCart(userId, productId);
  }

  @Override
  @PreAuthorize("hasAuthority('ROLE_ADMIN') || (hasAuthority('ROLE_USER') && @checkAccountIdUseCaseImpl.hasSameId(#userId))")
  public CartItemsResponseDTO getCartItems(Long userId, String lang) {
    return cartItemDTOMapper.toCartItemsResponseDTO(getCartItemsUseCase.getCartItems(userId, lang));
  }

  @Override
  @PreAuthorize("hasAuthority('ROLE_ADMIN') || (hasAuthority('ROLE_USER') && @checkAccountIdUseCaseImpl.hasSameId(#userId))")
  public UpdatedCartItemDTO setCartItemQuantity(Long userId, UUID productId, Integer quantity) {
    var updatedCartItemDto = updateCartItemQuantityUseCase.setQuantity(productId, userId, quantity);
    return cartItemDTOMapper.toUpdatedCartItemDTO(updatedCartItemDto);
  }
}
