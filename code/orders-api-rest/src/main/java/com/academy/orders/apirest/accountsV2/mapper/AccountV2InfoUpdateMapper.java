package com.academy.orders.apirest.accountsV2.mapper;

import com.academy.orders.domain.accountv2.dto.UpdateUserAccountV2InfoDto;
import com.academy.orders_api_rest.generated.model.UpdateAccountV2InfoRequestDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AccountV2InfoUpdateMapper {
  UpdateUserAccountV2InfoDto toUpdateUserAccountV2InfoDto(UpdateAccountV2InfoRequestDTO dto);
}
