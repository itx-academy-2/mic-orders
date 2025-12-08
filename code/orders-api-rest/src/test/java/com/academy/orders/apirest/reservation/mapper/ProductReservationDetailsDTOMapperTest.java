package com.academy.orders.apirest.reservation.mapper;

import com.academy.orders_api_rest.generated.model.PageProductReservationDetailsDTO;
import com.academy.orders_api_rest.generated.model.ProductReservationDetailsDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import java.time.ZoneOffset;
import java.util.List;

import static com.academy.orders.apirest.ModelUtils.createProductReservationDetails;
import static com.academy.orders.apirest.ModelUtils.getProductReservationDetailsPage;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ProductReservationDetailsDTOMapperTest {

  private ProductReservationDetailsDTOMapper mapper;

  @BeforeEach
  void setUp() {
    mapper = Mappers.getMapper(ProductReservationDetailsDTOMapper.class);
  }

  @Test
  void toDTOShouldMapAllFields() {
    // Given
    var reservation = createProductReservationDetails();

    // When
    ProductReservationDetailsDTO dto = mapper.toDTO(reservation);

    // Then
    assertEquals(reservation.username(), dto.getUsername());
    assertEquals(reservation.email(), dto.getEmail());
    assertEquals(reservation.quantity(), dto.getQuantity());
    assertEquals(reservation.addedAt().atOffset(ZoneOffset.UTC), dto.getAddedAt());
  }

  @Test
  void toPageProductReservationDetailsDTOShouldMapPage() {
    // Given
    var reservation = createProductReservationDetails();
    var page = getProductReservationDetailsPage(List.of(reservation), 0, 10);

    // When
    PageProductReservationDetailsDTO dtoPage = mapper.toPageProductReservationDetailsDTO(page);

    // Then
    assertNotNull(dtoPage);
    assertEquals(1, dtoPage.getContent().size());
    assertEquals(reservation.username(), dtoPage.getContent().get(0).getUsername());
    assertEquals(reservation.addedAt().atOffset(ZoneOffset.UTC), dtoPage.getContent().get(0).getAddedAt());
  }
}
