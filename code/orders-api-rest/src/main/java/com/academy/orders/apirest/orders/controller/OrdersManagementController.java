package com.academy.orders.apirest.orders.controller;

import com.academy.orders.apirest.common.mapper.PageableDTOMapper;
import com.academy.orders.apirest.orders.mapper.OrderDTOMapper;
import com.academy.orders.apirest.orders.mapper.OrderFilterParametersDTOMapper;
import com.academy.orders.apirest.orders.mapper.OrderStatusInfoDTOMapper;
import com.academy.orders.apirest.orders.mapper.PageOrderDTOMapper;
import com.academy.orders.apirest.orders.mapper.UpdateOrderStatusRequestDTOMapper;
import com.academy.orders.domain.common.Page;
import com.academy.orders.domain.common.Pageable;
import com.academy.orders.domain.order.dto.OrdersFilterParametersDto;
import com.academy.orders.domain.order.entity.Order;
import com.academy.orders.domain.order.entity.OrderManagement;
import com.academy.orders.domain.order.usecase.GetAllOrdersUseCase;
import com.academy.orders.domain.order.usecase.GetOrderByIdUseCase;
import com.academy.orders.domain.order.usecase.UpdateOrderStatusUseCase;
import com.academy.orders_api_rest.generated.api.OrdersManagementApi;
import com.academy.orders_api_rest.generated.model.ManagerOrderDTO;
import com.academy.orders_api_rest.generated.model.OrderStatusInfoDTO;
import com.academy.orders_api_rest.generated.model.OrdersFilterParametersDTO;
import com.academy.orders_api_rest.generated.model.PageManagerOrderPreviewDTO;
import com.academy.orders_api_rest.generated.model.PageableDTO;
import com.academy.orders_api_rest.generated.model.UpdateOrderStatusRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
public class OrdersManagementController implements OrdersManagementApi {
  private final UpdateOrderStatusUseCase updateOrderStatusUseCase;

  private final GetAllOrdersUseCase getAllOrdersUseCase;

  private final GetOrderByIdUseCase getOrderByIdUseCase;

  private final PageableDTOMapper pageableDTOMapper;

  private final PageOrderDTOMapper pageOrderDTOMapper;

  private final OrderFilterParametersDTOMapper orderFilterParametersDTOMapper;

  private final OrderDTOMapper orderDTOMapper;

  private final UpdateOrderStatusRequestDTOMapper updateOrderStatusRequestDTOMapper;

  private final OrderStatusInfoDTOMapper orderStatusInfoDTOMapper;

  @Override
  @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
  public ResponseEntity<PageManagerOrderPreviewDTO> getAllOrders(OrdersFilterParametersDTO ordersFilter, String lang,
      PageableDTO pageable) {
    Pageable pageableDomain = pageableDTOMapper.fromDto(pageable);
    OrdersFilterParametersDto filterParametersDto = orderFilterParametersDTOMapper.fromDTO(ordersFilter);
    Page<OrderManagement> ordersByUserId = getAllOrdersUseCase.getAllOrders(filterParametersDto, pageableDomain,
        getRole());
    return ResponseEntity.ok(pageOrderDTOMapper.toManagerDto(ordersByUserId));
  }

  @Override
  @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
  public ResponseEntity<OrderStatusInfoDTO> updateOrderStatus(UUID orderId,
      UpdateOrderStatusRequestDTO updateOrderStatusRequestDTO) {
    var updateOrderStatus = updateOrderStatusRequestDTOMapper.fromDTO(updateOrderStatusRequestDTO);
    var result = updateOrderStatusUseCase.updateOrderStatus(orderId, updateOrderStatus, getRole());
    OrderStatusInfoDTO orderStatusInfoDTO = orderStatusInfoDTOMapper.toDTO(result);
    return ResponseEntity.ok(orderStatusInfoDTO);
  }

  @Override
  @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
  public ResponseEntity<ManagerOrderDTO> getOrderById(UUID id, String lang) {
    Order order = getOrderByIdUseCase.getOrderById(id, lang);
    ManagerOrderDTO managerOrderDTO = orderDTOMapper.toManagerDto(order);
    return ResponseEntity.ok(managerOrderDTO);
  }

  private String getRole() {
    var authentication = SecurityContextHolder.getContext().getAuthentication();
    return authentication != null
        ? authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).findFirst().orElse(null)
        : null;
  }
}
