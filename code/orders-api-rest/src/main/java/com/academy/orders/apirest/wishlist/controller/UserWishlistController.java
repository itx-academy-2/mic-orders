package com.academy.orders.apirest.wishlist.controller;

import com.academy.orders.apirest.auth.util.SecurityUtils;
import com.academy.orders.apirest.common.mapper.PageableDTOMapper;
import com.academy.orders.apirest.products.mapper.ProductPreviewDTOMapper;
import com.academy.orders.domain.wishlist.usecase.AddToWishlistUseCase;
import com.academy.orders.domain.wishlist.usecase.GetUserWishlistUseCase;
import com.academy.orders.domain.wishlist.usecase.RemoveFromWishlistUseCase;
import com.academy.orders_api_rest.generated.api.WishlistApi;
import com.academy.orders_api_rest.generated.model.PageProductsDTO;
import com.academy.orders_api_rest.generated.model.PageableDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserWishlistController implements WishlistApi {

  private final AddToWishlistUseCase addToWishlistUseCase;

  private final RemoveFromWishlistUseCase removeFromWishlistUseCase;

  private final GetUserWishlistUseCase getUserWishlistUseCase;

  private final SecurityUtils securityUtils;

  private final PageableDTOMapper pageableDTOMapper;

  private final ProductPreviewDTOMapper productPreviewDTOMapper;

  @Override
  @PreAuthorize("hasAuthority('ROLE_USER')")
  public ResponseEntity<Void> addToWishlist(UUID productId) {
    var userId = securityUtils.getAuthenticatedUserId();
    log.info("User [{}] is adding product [{}] to wishlist", userId, productId);
    addToWishlistUseCase.addProductToWishlist(userId, productId);
    return ResponseEntity.noContent().build();
  }

  @Override
  @PreAuthorize("hasAuthority('ROLE_USER')")
  public ResponseEntity<PageProductsDTO> getUserWishlist(PageableDTO dto, String lang) {
    var userId = securityUtils.getAuthenticatedUserId();
    var pageable = pageableDTOMapper.fromDto(dto);
    log.info("User [{}] is fetching wishlist (page={}, size={}, sort={}) with language [{}]",
        userId, pageable.page(), pageable.size(), pageable.sort(), lang);
    var page = getUserWishlistUseCase.getProductsInUserWishlist(userId, lang, pageable);
    return ResponseEntity.ok(productPreviewDTOMapper.toPageProductsDTO(page));
  }

  @Override
  @PreAuthorize("hasAuthority('ROLE_USER')")
  public ResponseEntity<Void> removeFromWishlist(UUID productId) {
    var userId = securityUtils.getAuthenticatedUserId();
    log.info("User [{}] is removing product [{}] from wishlist", userId, productId);
    removeFromWishlistUseCase.removeProductFromWishlist(userId, productId);
    return ResponseEntity.noContent().build();
  }
}
