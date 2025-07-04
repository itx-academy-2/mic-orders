package com.academy.orders.domain.orderV2.entity;

import com.academy.orders.domain.account.entity.Account;
import com.academy.orders.domain.order.entity.OrderItem;
import com.academy.orders.domain.order.entity.OrderReceiver;
import com.academy.orders.domain.order.entity.PostAddress;
import com.academy.orders.domain.order.entity.enumerated.OrderStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
public record OrderV2(UUID id, OrderStatus orderStatus, OrderReceiver receiver, PostAddress postAddress, BigDecimal total,
                      Account account, List<OrderItem> orderItems, Boolean isPaid, LocalDateTime editedAt,
                      LocalDateTime createdAt, UUID postAddressId) {
}
