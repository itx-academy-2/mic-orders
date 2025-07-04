package com.academy.orders.infrastructure.orderV2;

import com.academy.orders.domain.orderV2.entity.OrderV2;
import com.academy.orders.infrastructure.order.entity.OrderEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderV2Mapper {
    OrderEntity toEntity(OrderV2 order);
}
