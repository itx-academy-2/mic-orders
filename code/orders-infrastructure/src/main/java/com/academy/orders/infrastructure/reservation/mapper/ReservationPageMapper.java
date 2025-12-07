package com.academy.orders.infrastructure.reservation.mapper;

import com.academy.orders.domain.reservation.entity.ProductReservationDetails;
import com.academy.orders.infrastructure.reservation.entity.projection.ReservationWithUserProjection;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

@Mapper(componentModel = "spring")
public interface ReservationPageMapper {
  com.academy.orders.domain.common.Page<ProductReservationDetails> fromProjection(Page<ReservationWithUserProjection> page);
}
