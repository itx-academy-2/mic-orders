package com.academy.orders.apirest.orders.controller;

import com.academy.orders.apirest.common.mapper.PageableDTOMapper;
import com.academy.orders.apirest.orders.mapper.OrderDTOMapper;
import com.academy.orders.apirest.orders.mapper.PageOrderDTOMapper;
import com.academy.orders.domain.common.Page;
import com.academy.orders.domain.common.Pageable;
import com.academy.orders.domain.order.entity.Order;
import com.academy.orders.domain.order.usecase.CancelOrderUseCase;
import com.academy.orders.domain.order.usecase.CreateOrderUseCase;
import com.academy.orders.domain.order.usecase.GetOrdersByUserIdUseCase;
import com.academy.orders_api_rest.generated.api.OrdersApi;
import com.academy.orders_api_rest.generated.model.PageUserOrderDTO;
import com.academy.orders_api_rest.generated.model.PageableDTO;
import com.academy.orders_api_rest.generated.model.PlaceOrderRequestDTO;
import com.academy.orders_api_rest.generated.model.PlaceOrderResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
@CrossOrigin
public class OrdersController implements OrdersApi {
  private final CreateOrderUseCase createOrderUseCase;

  private final GetOrdersByUserIdUseCase getOrdersByUserIdUseCase;

  private final CancelOrderUseCase cancelOrderUseCase;

  private final OrderDTOMapper mapper;

  private final PageableDTOMapper pageableDTOMapper;

  private final PageOrderDTOMapper pageOrderDTOMapper;

  @Override
  @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER') || (hasAnyAuthority('ROLE_USER') && @checkAccountIdUseCaseImpl.hasSameId(#userId))")
  public ResponseEntity<PlaceOrderResponseDTO> placeOrder(Long userId, PlaceOrderRequestDTO placeOrderRequestDTO) {
    var id = createOrderUseCase.createOrder(mapper.toCreateOrderDto(placeOrderRequestDTO), userId);
    PlaceOrderResponseDTO responseDTO = new PlaceOrderResponseDTO().orderId(id);
    return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO); // Return 201 Created with PlaceOrderResponseDTO
  }

  @Override
  @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER') || (hasAnyAuthority('ROLE_USER') && @checkAccountIdUseCaseImpl.hasSameId(#userId))")
  public ResponseEntity<PageUserOrderDTO> getOrdersByUser(Long userId, String language, PageableDTO pageable) {
    Pageable pageableDomain = pageableDTOMapper.fromDto(pageable);
    Page<Order> ordersByUserId = getOrdersByUserIdUseCase.getOrdersByUserId(userId, language, pageableDomain);
    PageUserOrderDTO pageUserOrderDTO = pageOrderDTOMapper.toUserDto(ordersByUserId);
    return ResponseEntity.ok(pageUserOrderDTO);
  }

  @Override
  @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER') || (hasAnyAuthority('ROLE_USER') && @checkAccountIdUseCaseImpl.hasSameId(#userId))")
  public ResponseEntity<Void> cancelOrder(Long userId, UUID orderId) {
    cancelOrderUseCase.cancelOrder(userId, orderId);
    return ResponseEntity.ok().build();
  }
}
