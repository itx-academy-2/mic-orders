package com.academy.orders.apirest.reservation.controller;

import com.academy.orders.apirest.common.mapper.PageableDTOMapper;
import com.academy.orders.apirest.reservation.mapper.ProductReservationDetailsDTOMapper;
import com.academy.orders.domain.common.Pageable;
import com.academy.orders.domain.reservation.usecase.GetProductReservationsInfoUseCase;
import com.academy.orders_api_rest.generated.api.ReservationsManagementApi;
import com.academy.orders_api_rest.generated.model.PageProductReservationDetailsDTO;
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
public class ReservationManagementController implements ReservationsManagementApi {

  private final GetProductReservationsInfoUseCase getProductReservationsInfoUseCase;

  private final PageableDTOMapper pageableDTOMapper;

  private final ProductReservationDetailsDTOMapper productReservationDetailsDTOMapper;

  @Override
  @PreAuthorize("hasAuthority('ROLE_MANAGER')")
  public ResponseEntity<PageProductReservationDetailsDTO> getProductReservationsInfo(UUID productId, PageableDTO dto) {
    Pageable pageable = pageableDTOMapper.fromDto(dto);
    log.info("Manager fetching reservations for product [{}] with pageable [{}]", productId, pageable);
    var page = getProductReservationsInfoUseCase.getProductReservationsInfo(productId, pageable);
    return ResponseEntity.ok(productReservationDetailsDTOMapper.toPageProductReservationDetailsDTO(page));
  }
}
