package com.academy.orders.domain.orderV2.dto;

import com.academy.orders.domain.order.entity.enumerated.DeliveryMethod;
import lombok.Builder;

@Builder
public record CreateOrderV2Dto(String firstName, String lastName, DeliveryMethod deliveryMethod,
    String city, String department, String phone, String title) {
}
