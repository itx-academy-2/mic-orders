package com.academy.orders.domain.orderV2.entity;

import com.academy.orders.domain.account.entity.Account;
import com.academy.orders.domain.accountV2.entity.AccountV2;
import com.academy.orders.domain.order.entity.enumerated.DeliveryMethod;
import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record PostAddressV2(UUID id, DeliveryMethod deliveryMethod, String city, String department, String recipientFirstName,
                            String recipientLastName, String recipientPhone, String title, AccountV2 account, List<OrderV2> orders) {
}
