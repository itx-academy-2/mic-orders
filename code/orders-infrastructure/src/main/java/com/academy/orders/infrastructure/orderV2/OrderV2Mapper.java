package com.academy.orders.infrastructure.orderV2;

import com.academy.orders.domain.orderV2.entity.OrderV2;
import com.academy.orders.infrastructure.orderV2.entity.OrderV2Entity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderV2Mapper {
    OrderV2Entity toEntity(OrderV2 order);
}
