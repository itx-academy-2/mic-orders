package com.academy.orders.infrastructure.postaddress;

import com.academy.orders.domain.postaddress.entity.PostAddressV2;
import com.academy.orders.infrastructure.postaddress.entity.PostAddressV2Entity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PostAddressV2Mapper {
    PostAddressV2Entity toEntity(PostAddressV2 postAddressV2);
}
