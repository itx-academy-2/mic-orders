package com.academy.orders.apirest.ordersV2.controller;

import com.academy.orders.apirest.ordersV2.mapper.OrderV2DTOMapper;
import com.academy.orders.domain.orderV2.usecase.CreateOrderV2UseCase;
import com.academy.orders_api_rest.generated.api.OrdersV2Api;
import com.academy.orders_api_rest.generated.model.PlaceOrderRequestV2DTO;
import com.academy.orders_api_rest.generated.model.PlaceOrderResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@CrossOrigin
public class OrdersV2Controller implements OrdersV2Api {
    private final CreateOrderV2UseCase createOrderV2UseCase;

    private final OrderV2DTOMapper mapper;

    @Override
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER') || (hasAnyAuthority('ROLE_USER') && @checkAccountIdUseCaseImpl.hasSameId(#userId))")
    public ResponseEntity<PlaceOrderResponseDTO> placeOrderV2(Long userId, PlaceOrderRequestV2DTO placeOrderRequestV2DTO) {
        log.info("POST /v2/users/{userId}/orders - userId: {}", userId);
        var id = createOrderV2UseCase.createOrderV2(mapper.toCreateOrderV2Dto(placeOrderRequestV2DTO), userId);
        PlaceOrderResponseDTO responseDTO = new PlaceOrderResponseDTO().orderId(id);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }
}
