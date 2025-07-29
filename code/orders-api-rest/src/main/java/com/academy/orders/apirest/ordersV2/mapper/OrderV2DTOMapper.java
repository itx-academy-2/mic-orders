package com.academy.orders.apirest.ordersV2.mapper;

import com.academy.orders.apirest.products.mapper.ProductPreviewDTOMapper;
import com.academy.orders.domain.orderV2.dto.CreateOrderV2Dto;
import com.academy.orders_api_rest.generated.model.PlaceOrderRequestV2DTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderV2DTOMapper {
  CreateOrderV2Dto toCreateOrderV2Dto(PlaceOrderRequestV2DTO placeOrderRequestV2DTO);
}
