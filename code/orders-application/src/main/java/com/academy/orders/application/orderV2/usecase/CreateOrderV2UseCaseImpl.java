package com.academy.orders.application.orderV2.usecase;

import com.academy.orders.domain.orderV2.dto.CreateOrderV2Dto;
import com.academy.orders.domain.orderV2.usecase.CreateOrderV2UseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreateOrderV2UseCaseImpl implements CreateOrderV2UseCase {
    @Override
    public UUID createOrderV2(CreateOrderV2Dto order, Long accountId) {
        return null;
    }
}
