package com.academy.orders.infrastructure.postaddress;

import com.academy.orders.domain.postaddress.entity.PostAddressV2;
import com.academy.orders.infrastructure.order.entity.PostAddressEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PostAddressV2Mapper {
    PostAddressEntity toEntity(PostAddressV2 postAddressV2);
}
