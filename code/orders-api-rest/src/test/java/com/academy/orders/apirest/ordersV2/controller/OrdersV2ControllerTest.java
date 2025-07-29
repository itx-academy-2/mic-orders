package com.academy.orders.apirest.ordersV2.controller;

import com.academy.orders.apirest.auth.util.SecurityUtils;
import com.academy.orders.apirest.auth.validator.CheckAccountIdUseCaseImpl;
import com.academy.orders.apirest.common.ErrorHandler;
import com.academy.orders.apirest.common.TestSecurityConfig;
import com.academy.orders.apirest.ordersV2.mapper.OrderV2DTOMapper;
import com.academy.orders.domain.cart.exception.EmptyCartException;
import com.academy.orders.domain.orderV2.dto.CreateOrderV2Dto;
import com.academy.orders.domain.orderV2.usecase.CreateOrderV2UseCase;
import com.academy.orders_api_rest.generated.model.PlaceOrderRequestV2DTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.aop.AopAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static com.academy.orders.apirest.ModelUtils.getPlaceOrderRequestV2DTO;
import static com.academy.orders.apirest.ModelUtils.getJwtRequest;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrdersV2Controller.class)
@ContextConfiguration(classes = {OrdersV2Controller.class})
@Import(value = {CheckAccountIdUseCaseImpl.class, AopAutoConfiguration.class, TestSecurityConfig.class,
    ErrorHandler.class, SecurityUtils.class})
public class OrdersV2ControllerTest {
  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private CreateOrderV2UseCase createOrderV2UseCase;

  @MockBean
  private OrderV2DTOMapper mapper;

  @Test
  @SneakyThrows
  void placeOrderV2_AdminAccessAllowed_Test() {
    // Given
    Long userId = 1L;
    String role = "ROLE_ADMIN";
    var orderId = UUID.randomUUID();

    when(mapper.toCreateOrderV2Dto(any(PlaceOrderRequestV2DTO.class))).thenReturn(CreateOrderV2Dto.builder().build());
    when(createOrderV2UseCase.createOrderV2(any(CreateOrderV2Dto.class), eq(userId))).thenReturn(orderId);

    // When
    var result = mockMvc.perform(post("/v2/users/{id}/orders", userId).with(getJwtRequest(userId, role))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(getPlaceOrderRequestV2DTO())));

    // Then
    result.andExpect(status().isCreated());
    result.andExpect(jsonPath("$.orderId").value(orderId.toString()));
    verify(mapper).toCreateOrderV2Dto(any(PlaceOrderRequestV2DTO.class));
    verify(createOrderV2UseCase).createOrderV2(any(CreateOrderV2Dto.class), anyLong());
  }

  @Test
  @SneakyThrows
  void placeOrderV2_ManagerAccessAllowed_Test() {
    // Given
    Long userId = 1L;
    String role = "ROLE_MANAGER";
    var orderId = UUID.randomUUID();

    when(mapper.toCreateOrderV2Dto(any(PlaceOrderRequestV2DTO.class))).thenReturn(CreateOrderV2Dto.builder().build());
    when(createOrderV2UseCase.createOrderV2(any(CreateOrderV2Dto.class), eq(userId))).thenReturn(orderId);

    // When
    var result = mockMvc.perform(post("/v2/users/{id}/orders", userId).with(getJwtRequest(userId, role))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(getPlaceOrderRequestV2DTO())));

    // Then
    result.andExpect(status().isCreated());
    result.andExpect(jsonPath("$.orderId").value(orderId.toString()));
    verify(mapper).toCreateOrderV2Dto(any(PlaceOrderRequestV2DTO.class));
    verify(createOrderV2UseCase).createOrderV2(any(CreateOrderV2Dto.class), anyLong());
  }

  @Test
  @SneakyThrows
  void placeOrderV2_UserAllowedToCreateOrderForThemselves_Test() {
    // Given
    Long userId = 1L;
    String role = "ROLE_USER";
    var orderId = UUID.randomUUID();

    when(mapper.toCreateOrderV2Dto(any(PlaceOrderRequestV2DTO.class))).thenReturn(CreateOrderV2Dto.builder().build());
    when(createOrderV2UseCase.createOrderV2(any(CreateOrderV2Dto.class), eq(userId))).thenReturn(orderId);

    // When
    var result = mockMvc.perform(post("/v2/users/{id}/orders", userId).with(getJwtRequest(userId, role))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(getPlaceOrderRequestV2DTO())));

    // Then
    result.andExpect(status().isCreated());
    result.andExpect(jsonPath("$.orderId").value(orderId.toString()));
    verify(mapper).toCreateOrderV2Dto(any(PlaceOrderRequestV2DTO.class));
    verify(createOrderV2UseCase).createOrderV2(any(CreateOrderV2Dto.class), anyLong());
  }

  @Test
  @SneakyThrows
  void placeOrderV2_UserNotAllowedToPlaceOrderForADifferentUser_Test() {
    // Given
    Long actualUserId = 1L;
    Long requestedUserId = 99L;
    String role = "ROLE_USER";

    // When
    var result = mockMvc.perform(post("/v2/users/{id}/orders", requestedUserId).with(getJwtRequest(actualUserId, role))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(getPlaceOrderRequestV2DTO())));

    // Then
    result.andExpect(status().isForbidden());
    verify(mapper, never()).toCreateOrderV2Dto(any(PlaceOrderRequestV2DTO.class));
    verify(createOrderV2UseCase, never()).createOrderV2(any(CreateOrderV2Dto.class), anyLong());
  }

  @Test
  @SneakyThrows
  void placeOrderV2_ThrowsEmptyCartException_Test() {
    // Given
    Long userId = 1L;
    String role = "ROLE_ADMIN";

    when(mapper.toCreateOrderV2Dto(any(PlaceOrderRequestV2DTO.class))).thenReturn(CreateOrderV2Dto.builder().build());
    when(createOrderV2UseCase.createOrderV2(any(CreateOrderV2Dto.class), anyLong()))
        .thenThrow(new EmptyCartException());

    // When
    var result = mockMvc.perform(post("/v2/users/{id}/orders", userId).with(getJwtRequest(userId, role))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(getPlaceOrderRequestV2DTO())));

    // Then
    result.andExpect(status().isBadRequest());
    verify(mapper).toCreateOrderV2Dto(any(PlaceOrderRequestV2DTO.class));
    verify(createOrderV2UseCase).createOrderV2(any(CreateOrderV2Dto.class), anyLong());
  }
}
