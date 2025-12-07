package com.academy.orders.apirest.reservation.mapper;

import com.academy.orders.domain.common.Page;
import com.academy.orders.domain.reservation.entity.ProductReservationDetails;
import com.academy.orders_api_rest.generated.model.PageProductReservationDetailsDTO;
import com.academy.orders_api_rest.generated.model.ProductReservationDetailsDTO;
import org.mapstruct.Mapper;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Mapper(componentModel = "spring")
public interface ProductReservationDetailsDTOMapper {

  ProductReservationDetailsDTO toDTO(ProductReservationDetails details);

  PageProductReservationDetailsDTO toPageProductReservationDetailsDTO(Page<ProductReservationDetails> page);

  default OffsetDateTime map(Instant instant) {
    return instant.atOffset(ZoneOffset.UTC);
  }
}
