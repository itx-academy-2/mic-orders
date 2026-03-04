package com.academy.orders.apirest.reservation.mapper;

import com.academy.orders.domain.reservation.entity.ReservationMetadata;
import com.academy.orders.domain.reservation.entity.UserReservationsMetadata;
import com.academy.orders_api_rest.generated.model.ReservedProductMetadataDTO;
import com.academy.orders_api_rest.generated.model.UserReservationsMetadataDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class UserReservationsMetadataMapperTest {

  private UserReservationsMetadataMapper mapper;

  @BeforeEach
  void setUp() {
    mapper = Mappers.getMapper(UserReservationsMetadataMapper.class);
  }

  @Test
  void toDTOShouldMapAllFields() {
    // Given
    var reservationMetadata = createReservationMetadata();
    var domain = new UserReservationsMetadata(BigDecimal.valueOf(1500), 2, List.of(reservationMetadata));

    // When
    UserReservationsMetadataDTO dto = mapper.toDTO(domain);

    // Then
    assertNotNull(dto);
    assertEquals(1500d, dto.getRemainingMoney());
    assertEquals(2, dto.getRemainingItems());
    assertEquals(1, dto.getReservations().size());

    var reservedProduct = dto.getReservations().get(0);
    assertEquals(reservationMetadata.productId(), reservedProduct.getId());
    assertEquals(reservationMetadata.reservedAt().toString(), reservedProduct.getReservedAt());
  }

  @Test
  void toDTOShouldMapSingleReservationMetadata() {
    // Given
    var reservationMetadata = createReservationMetadata();

    // When
    ReservedProductMetadataDTO dto = mapper.toDTO(reservationMetadata);

    // Then
    assertEquals(reservationMetadata.productId(), dto.getId());
    assertEquals(reservationMetadata.reservedAt().toString(), dto.getReservedAt());
  }

  private ReservationMetadata createReservationMetadata() {
    return new ReservationMetadata(UUID.randomUUID(), Instant.parse("2025-03-03T10:15:30Z"));
  }
}
