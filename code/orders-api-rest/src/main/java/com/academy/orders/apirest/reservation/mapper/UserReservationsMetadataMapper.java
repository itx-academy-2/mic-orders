package com.academy.orders.apirest.reservation.mapper;

import com.academy.orders.domain.reservation.entity.ReservationMetadata;
import com.academy.orders.domain.reservation.entity.UserReservationsMetadata;
import com.academy.orders_api_rest.generated.model.ReservedProductMetadataDTO;
import com.academy.orders_api_rest.generated.model.UserReservationsMetadataDTO;
import org.mapstruct.Mapper;

import java.time.ZoneOffset;

@Mapper(componentModel = "spring")
public interface UserReservationsMetadataMapper {

  default ReservedProductMetadataDTO toDTO(ReservationMetadata metadata) {
    return new ReservedProductMetadataDTO(metadata.productId(), metadata.reservedAt().atOffset(ZoneOffset.UTC));
  }

  default UserReservationsMetadataDTO toDTO(UserReservationsMetadata domain) {
    UserReservationsMetadataDTO dto = new UserReservationsMetadataDTO();
    dto.setRemainingMoney(domain.remainingMoney().toString());
    dto.setRemainingItems(domain.remainingItems());
    dto.setReservations(domain.reservations().stream()
        .map(this::toDTO)
        .toList());
    return dto;
  }
}
