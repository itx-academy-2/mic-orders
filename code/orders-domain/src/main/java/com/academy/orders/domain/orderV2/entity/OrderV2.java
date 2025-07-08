package com.academy.orders.domain.orderV2.entity;

import com.academy.orders.domain.accountV2.entity.AccountV2;
import com.academy.orders.domain.order.entity.OrderItem;
import com.academy.orders.domain.order.entity.enumerated.OrderStatus;
import com.academy.orders.domain.postaddress.entity.PostAddressV2;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
public record OrderV2(UUID id, OrderStatus orderStatus, PostAddressV2 postAddress, BigDecimal total,
                      AccountV2 account, List<OrderItem> orderItems, Boolean isPaid, LocalDateTime editedAt,
                      LocalDateTime createdAt) {
}
