package com.academy.orders.infrastructure.postaddress.repository;

import com.academy.orders.infrastructure.order.entity.PostAddressEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PostAddressJpaAdapter extends CrudRepository<PostAddressEntity, UUID> {
}
