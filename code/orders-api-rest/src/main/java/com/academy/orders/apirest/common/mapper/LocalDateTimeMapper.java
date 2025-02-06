package com.academy.orders.apirest.common.mapper;

import org.mapstruct.Mapper;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Mapper(componentModel = "spring")
public interface LocalDateTimeMapper {
  default OffsetDateTime map(LocalDateTime value) {
    return OffsetDateTime.of(value, ZoneOffset.UTC);
  }

  default LocalDateTime map(OffsetDateTime value) {
    if (value == null)
      return null;
    return value.toLocalDateTime();
  }
}
