package com.academy.orders.apirest.orders.mapper;

import com.academy.orders.apirest.common.mapper.LocalDateTimeMapper;
import com.academy.orders.domain.order.dto.OrdersFilterParametersDto;
import com.academy.orders_api_rest.generated.model.OrdersFilterParametersDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {LocalDateTimeMapper.class})
public interface OrderFilterParametersDTOMapper {
  OrdersFilterParametersDto fromDTO(OrdersFilterParametersDTO ordersFilterParametersDTO);
}
