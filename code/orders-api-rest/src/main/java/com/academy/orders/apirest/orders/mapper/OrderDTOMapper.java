package com.academy.orders.apirest.orders.mapper;

import com.academy.orders.apirest.common.mapper.LocalDateTimeMapper;
import com.academy.orders.apirest.products.mapper.ProductPreviewDTOMapper;
import com.academy.orders.domain.order.dto.CreateOrderDto;
import com.academy.orders.domain.order.entity.Order;
import com.academy.orders.domain.order.entity.OrderManagement;
import com.academy.orders_api_rest.generated.model.ManagerOrderDTO;
import com.academy.orders_api_rest.generated.model.ManagerOrderPreviewDTO;
import com.academy.orders_api_rest.generated.model.PlaceOrderRequestDTO;
import com.academy.orders_api_rest.generated.model.UserOrderDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {ProductPreviewDTOMapper.class, LocalDateTimeMapper.class})
public interface OrderDTOMapper {
  @Mapping(target = "totalWithDiscount", expression = "java(order.getTotalWithDiscount())")
  UserOrderDTO toUserDto(Order order);

  @Mapping(target = "totalWithDiscount", expression = "java(order.getTotalWithDiscount())")
  ManagerOrderDTO toManagerDto(Order order);

  @Mapping(target = "totalWithDiscount", expression = "java(orderManagement.getTotalWithDiscount())")
  ManagerOrderPreviewDTO toManagerOrderPreviewDTOFromOrderMana(OrderManagement orderManagement);

  CreateOrderDto toCreateOrderDto(PlaceOrderRequestDTO placeOrderRequestDTO);
}
