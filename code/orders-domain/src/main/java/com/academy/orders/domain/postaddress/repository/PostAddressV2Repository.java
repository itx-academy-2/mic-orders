package com.academy.orders.domain.postaddress.repository;

import com.academy.orders.domain.postaddress.entity.PostAddressV2;

import java.util.List;

public interface PostAddressV2Repository {
    List<PostAddressV2> getPermanentPostAddressesByUserId(Long userId);
}
