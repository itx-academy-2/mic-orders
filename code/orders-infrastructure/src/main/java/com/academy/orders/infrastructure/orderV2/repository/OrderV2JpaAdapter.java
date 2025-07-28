package com.academy.orders.infrastructure.orderV2.repository;

import com.academy.orders.infrastructure.orderV2.entity.OrderV2Entity;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface OrderV2JpaAdapter extends CrudRepository<OrderV2Entity, UUID> {
}
