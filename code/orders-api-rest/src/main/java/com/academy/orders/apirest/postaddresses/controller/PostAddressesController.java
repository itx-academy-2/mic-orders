package com.academy.orders.apirest.postaddresses.controller;

import com.academy.orders.apirest.postaddresses.mapper.UserPostAddressResponseDTOMapper;
import com.academy.orders.domain.postaddress.usecase.DeletePermanentPostAddressesUseCase;
import com.academy.orders.domain.postaddress.usecase.GetPermanentPostAddressesUseCase;
import com.academy.orders_api_rest.generated.api.AddressApi;
import com.academy.orders_api_rest.generated.model.UserPostAddressResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class PostAddressesController implements AddressApi {
  private final UserPostAddressResponseDTOMapper mapper;

  private final GetPermanentPostAddressesUseCase useCase;

  private final DeletePermanentPostAddressesUseCase deleteUseCase;

  @Override
  @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER') || (hasAnyAuthority('ROLE_USER') && @checkAccountIdUseCaseImpl.hasSameId(#userId))")
  public ResponseEntity<List<UserPostAddressResponseDTO>> getUserAddresses(Long userId) {
    var userPostAddresses = useCase.getPermanentPostAddressesByUserId(userId).stream().map(mapper::toUserPostAddressResponseDTO).toList();
    return ResponseEntity.status(HttpStatus.OK).body(userPostAddresses);
  }

  @Override
  @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER') || (hasAnyAuthority('ROLE_USER') && @checkAccountIdUseCaseImpl.hasSameId(#userId))")
  public ResponseEntity<Void> removeUserPermanentAddress(Long userId, UUID addressId) {
    deleteUseCase.deletePermanentAddress(userId, addressId);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }
}
