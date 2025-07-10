package com.academy.orders.infrastructure.orderV2.entity;

import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.UUID;

@Data
@Embeddable
@NoArgsConstructor
@ToString
public class OrderItemV2Id implements Serializable {
    private UUID orderV2Id;

    private UUID productId;
}
