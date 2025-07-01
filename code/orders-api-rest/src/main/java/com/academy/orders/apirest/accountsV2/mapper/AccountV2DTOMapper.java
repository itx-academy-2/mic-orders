package com.academy.orders.apirest.accountsV2.mapper;

import com.academy.orders.domain.accountv2.entity.AccountV2;
import com.academy.orders_api_rest.generated.model.UserAccountInfoDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Mapper(componentModel = "spring")
public interface AccountV2DTOMapper {

  @Mapping(source = "createdAt", target = "createdAt", qualifiedByName = "mapLocalDateTimeToOffsetDateTime")
  UserAccountInfoDTO toUserAccountInfoDto(AccountV2 account);

  @Named("mapLocalDateTimeToOffsetDateTime")
  default OffsetDateTime mapLocalDateTimeToOffsetDateTime(LocalDateTime localDateTime) {
    return localDateTime == null ? null : localDateTime.atOffset(ZoneOffset.UTC);
  }
}
