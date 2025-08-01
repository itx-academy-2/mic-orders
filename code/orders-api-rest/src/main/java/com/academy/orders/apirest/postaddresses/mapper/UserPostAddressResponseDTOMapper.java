package com.academy.orders.apirest.postaddresses.mapper;

import com.academy.orders.domain.postaddress.entity.PostAddressV2;
import com.academy.orders_api_rest.generated.model.UserPostAddressResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserPostAddressResponseDTOMapper {

  @Mapping(source = "recipientFirstName", target = "firstName")
  @Mapping(source = "recipientLastName", target = "lastName")
  @Mapping(source = "recipientPhone", target = "phone")
  UserPostAddressResponseDTO toUserPostAddressResponseDTO(PostAddressV2 postAddressV2);
}
