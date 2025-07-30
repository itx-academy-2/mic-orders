package com.academy.orders.domain.postaddress.usecase;

import com.academy.orders.domain.postaddress.entity.PostAddressV2;

import java.util.List;

public interface GetPermanentPostAddressesUseCase {
    List<PostAddressV2> getPermanentPostAddressesByUserId(Long userId);
}
