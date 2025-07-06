package com.academy.orders.infrastructure.orderV2;

import com.academy.orders.domain.orderV2.entity.PostAddressV2;
import com.academy.orders.infrastructure.order.entity.PostAddressEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PostAddressV2Mapper {
    PostAddressEntity toEntity(PostAddressV2 postAddressV2);
}
