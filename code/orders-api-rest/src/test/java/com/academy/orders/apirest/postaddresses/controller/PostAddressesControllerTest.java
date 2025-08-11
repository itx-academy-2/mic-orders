package com.academy.orders.apirest.postaddresses.controller;

import com.academy.orders.apirest.auth.util.SecurityUtils;
import com.academy.orders.apirest.auth.validator.CheckAccountIdUseCaseImpl;
import com.academy.orders.apirest.common.ErrorHandler;
import com.academy.orders.apirest.common.TestSecurityConfig;
import com.academy.orders.apirest.postaddresses.mapper.UserPostAddressResponseDTOMapper;
import com.academy.orders.domain.account.exception.AccountNotFoundException;
import com.academy.orders.domain.postaddress.entity.PostAddressV2;
import com.academy.orders.domain.postaddress.exception.PostAddressNotFoundException;
import com.academy.orders.domain.postaddress.usecase.DeletePermanentPostAddressesUseCase;
import com.academy.orders.domain.postaddress.usecase.GetPermanentPostAddressesUseCase;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.aop.AopAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static com.academy.orders.apirest.ModelUtils.getPostAddressV2WithNewData;
import static com.academy.orders.apirest.ModelUtils.getPostAddressV2;
import static com.academy.orders.apirest.ModelUtils.getUserPostAddressResponseDTO;
import static com.academy.orders.apirest.ModelUtils.getJwtRequest;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PostAddressesController.class)
@ContextConfiguration(classes = {PostAddressesController.class})
@Import(value = {CheckAccountIdUseCaseImpl.class, AopAutoConfiguration.class, TestSecurityConfig.class,
    ErrorHandler.class, SecurityUtils.class})
public class PostAddressesControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private UserPostAddressResponseDTOMapper mapper;

  @MockBean
  private GetPermanentPostAddressesUseCase useCase;

  @MockBean
  private DeletePermanentPostAddressesUseCase deleteUseCase;

  @Test
  @SneakyThrows
  void getUserAddresses_AdminAccessAllowed_Test() {
    // Given
    Long userId = 23L;
    Long adminId = 1L;
    String role = "ROLE_ADMIN";

    performSuccessfulAccessTest(adminId, userId, role);
  }

  @Test
  @SneakyThrows
  void getUserAddresses_ManagerAccessAllowed_Test() {
    // Given
    Long userId = 23L;
    Long managerId = 2L;
    String role = "ROLE_MANAGER";

    performSuccessfulAccessTest(managerId, userId, role);
  }

  @Test
  @SneakyThrows
  void getUserAddresses_UserAllowedToGetTheirAddresses_Test() {
    // Given
    Long userIdWanted = 23L;
    Long userIdExisting = 23L;
    String role = "ROLE_USER";

    performSuccessfulAccessTest(userIdExisting, userIdWanted, role);
  }

  @Test
  @SneakyThrows
  void getUserAddresses_UserNotAllowedToGetOthersAddresses_Test() {
    // Given
    Long userIdWanted = 23L;
    Long userIdExisting = 55L;
    String role = "ROLE_USER";
    var postAddressV2First = getPostAddressV2();
    var userPostAddressResponseDTOFirst = getUserPostAddressResponseDTO(postAddressV2First);
    var postAddressV2Second = getPostAddressV2WithNewData();
    var userPostAddressResponseDTOSecond = getUserPostAddressResponseDTO(postAddressV2Second);

    when(mapper.toUserPostAddressResponseDTO(postAddressV2First)).thenReturn(userPostAddressResponseDTOFirst);
    when(mapper.toUserPostAddressResponseDTO(postAddressV2Second)).thenReturn(userPostAddressResponseDTOSecond);
    when(useCase.getPermanentPostAddressesByUserId(userIdWanted)).thenReturn(List.of(postAddressV2First, postAddressV2Second));

    // When
    var result = mockMvc.perform(get("/v1/users/{userId}/addresses", userIdWanted).with(getJwtRequest(userIdExisting, role)));

    // Then
    verify(mapper, never()).toUserPostAddressResponseDTO(any(PostAddressV2.class));
    verify(useCase, never()).getPermanentPostAddressesByUserId(userIdWanted);
    result.andExpect(status().isForbidden());
  }

  private void performSuccessfulAccessTest(Long userId, Long requesterId, String role) throws Exception {
    // Given
    var postAddressV2First = getPostAddressV2();
    var userPostAddressResponseDTOFirst = getUserPostAddressResponseDTO(postAddressV2First);
    var postAddressV2Second = getPostAddressV2WithNewData();
    var userPostAddressResponseDTOSecond = getUserPostAddressResponseDTO(postAddressV2Second);

    when(mapper.toUserPostAddressResponseDTO(postAddressV2First)).thenReturn(userPostAddressResponseDTOFirst);
    when(mapper.toUserPostAddressResponseDTO(postAddressV2Second)).thenReturn(userPostAddressResponseDTOSecond);
    when(useCase.getPermanentPostAddressesByUserId(userId)).thenReturn(List.of(postAddressV2First, postAddressV2Second));

    // When
    var result = mockMvc.perform(get("/v1/users/{userId}/addresses", userId).with(getJwtRequest(requesterId, role)));

    // Then
    verify(mapper, times(2)).toUserPostAddressResponseDTO(any(PostAddressV2.class));
    verify(useCase).getPermanentPostAddressesByUserId(userId);
    result.andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(2))
        .andExpect(jsonPath("$[0].id").value(userPostAddressResponseDTOFirst.getId().toString()))
        .andExpect(jsonPath("$[1].id").value(userPostAddressResponseDTOSecond.getId().toString()));
  }

  @Test
  @SneakyThrows
  void removeUserPermanentAddress_Success_Test() {
    // Given
    Long userIdWanted = 23L;
    Long userIdExisting = 23L;
    String role = "ROLE_USER";
    UUID addressId = UUID.fromString("4b08cd5a-a34e-4bf1-aabf-f2e79740919b");

    doNothing().when(deleteUseCase).deletePermanentAddress(userIdWanted, addressId);

    //When
    var result = mockMvc.perform(delete("/v1/users/{userId}/addresses/{addressId}", userIdWanted, addressId).with(getJwtRequest(userIdExisting, role)));

    //Then
    result.andExpect(status().isNoContent());
    verify(deleteUseCase).deletePermanentAddress(userIdWanted, addressId);
  }

  @Test
  @SneakyThrows
  void removeUserPermanentAddress_UserNotFound_Test() {
    // Given
    Long userIdWanted = 27L;
    Long adminId = 1L;
    String role = "ROLE_ADMIN";
    UUID addressId = UUID.fromString("4b08cd5a-a34e-4bf1-aabf-f2e79740919b");

    doThrow(new AccountNotFoundException(userIdWanted)).when(deleteUseCase).deletePermanentAddress(userIdWanted, addressId);

    //When
    var result = mockMvc.perform(delete("/v1/users/{userId}/addresses/{addressId}", userIdWanted, addressId).with(getJwtRequest(adminId, role)));

    //Then
    result.andExpect(status().isNotFound())
            .andExpect(jsonPath("$.detail").value("Account with id: 27 is not found"));
    verify(deleteUseCase).deletePermanentAddress(userIdWanted, addressId);
  }

  @Test
  @SneakyThrows
  void removeUserPermanentAddress_PostAddressNotFound_Test() {
    // Given
    Long userIdWanted = 23L;
    Long userIdExisting = 23L;
    String role = "ROLE_USER";
    UUID addressId = UUID.fromString("7b08cd5a-a34e-4bf1-aabf-f2e79740919b");

    doThrow(new PostAddressNotFoundException(addressId)).when(deleteUseCase).deletePermanentAddress(userIdWanted, addressId);

    //When
    var result = mockMvc.perform(delete("/v1/users/{userId}/addresses/{addressId}", userIdWanted, addressId).with(getJwtRequest(userIdExisting, role)));

    //Then
    result.andExpect(status().isNotFound())
            .andExpect(jsonPath("$.detail").value("PostAddress with id: 7b08cd5a-a34e-4bf1-aabf-f2e79740919b is not found"));
    verify(deleteUseCase).deletePermanentAddress(userIdWanted, addressId);
  }

  @Test
  @SneakyThrows
  void removeUserPermanentAddress_UserIsNotAllowedToDeleteOtherUsersAddresses_Test() {
    // Given
    Long userIdWanted = 27L;
    Long userIdExisting = 23L;
    String role = "ROLE_USER";
    UUID addressId = UUID.fromString("7b08cd5a-a34e-4bf1-aabf-f2e79740919b");

    //When
    var result = mockMvc.perform(delete("/v1/users/{userId}/addresses/{addressId}", userIdWanted, addressId).with(getJwtRequest(userIdExisting, role)));

    //Then
    result.andExpect(status().isForbidden());
    verify(deleteUseCase, never()).deletePermanentAddress(userIdWanted, addressId);
  }
}
