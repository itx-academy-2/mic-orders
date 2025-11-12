package com.academy.orders.apirest.reservation.controller;

import com.academy.orders.apirest.auth.util.SecurityUtils;
import com.academy.orders.apirest.products.mapper.ProductPreviewDTOMapper;
import com.academy.orders.domain.reservation.usecase.AddToUserReservationsUseCase;
import com.academy.orders.domain.reservation.usecase.GetUserReservationsUseCase;
import com.academy.orders.domain.reservation.usecase.RemoveFromUserReservationsUseCase;
import com.academy.orders_api_rest.generated.api.ReservationsApi;
import com.academy.orders_api_rest.generated.model.ProductPreviewDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserReservationController implements ReservationsApi {

  private final AddToUserReservationsUseCase addToUserReservationsUseCase;

  private final RemoveFromUserReservationsUseCase removeFromUserReservationsUseCase;

  private final GetUserReservationsUseCase getUserReservationsUseCase;

  private final SecurityUtils securityUtils;

  private final ProductPreviewDTOMapper productPreviewDTOMapper;

  @Override
  @PreAuthorize("hasAuthority('ROLE_USER')")
  public ResponseEntity<Void> addToReservations(UUID productId) {
    var userId = securityUtils.getAuthenticatedUserId();
    log.info("User [{}] is adding product [{}] to reservations", userId, productId);
    addToUserReservationsUseCase.addProductToReservations(userId, productId);
    return ResponseEntity.noContent().build();
  }

  @Override
  @PreAuthorize("hasAuthority('ROLE_USER')")
  public ResponseEntity<List<ProductPreviewDTO>> getUserReservations(String lang) {
    var userId = securityUtils.getAuthenticatedUserId();
    log.info("User [{}] is fetching reservations with language [{}]", userId, lang);
    var products = getUserReservationsUseCase.getUserReservations(userId, lang);
    return ResponseEntity.ok(productPreviewDTOMapper.toProductPreviewListDTO(products));
  }

  @Override
  @PreAuthorize("hasAuthority('ROLE_USER')")
  public ResponseEntity<Void> removeFromReservations(UUID productId) {
    var userId = securityUtils.getAuthenticatedUserId();
    log.info("User [{}] is removing product [{}] from reservations", userId, productId);
    removeFromUserReservationsUseCase.removeProductFromReservations(userId, productId);
    return ResponseEntity.noContent().build();
  }
}
