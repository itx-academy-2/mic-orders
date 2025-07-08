package com.academy.orders.apirest.accountsV2.controller;

import com.academy.orders.apirest.accountsV2.mapper.AccountV2DTOMapper;
import com.academy.orders.apirest.accountsV2.mapper.AccountV2InfoUpdateMapper;
import com.academy.orders.apirest.auth.util.SecurityUtils;
import com.academy.orders.apirest.common.ErrorHandler;
import com.academy.orders.domain.account.exception.AccountNotFoundException;
import com.academy.orders.domain.accountv2.usecase.GetUserAccountV2InfoUseCase;
import com.academy.orders.domain.accountv2.usecase.UpdateUserAccountV2InfoUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static com.academy.orders.apirest.ModelUtils.getAccountV2;
import static com.academy.orders.apirest.ModelUtils.getJwtRequest;
import static com.academy.orders.apirest.ModelUtils.getUpdateAccountV2InfoRequestDTO;
import static com.academy.orders.apirest.ModelUtils.getUpdateUserAccountV2InfoDto;
import static com.academy.orders.apirest.ModelUtils.getUserAccountInfoDTO;
import static com.academy.orders.apirest.TestConstants.ROLE_USER;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserCabinetController.class)
@ContextConfiguration(classes = UserCabinetController.class)
@Import(ErrorHandler.class)
class UserCabinetControllerTest {
  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private GetUserAccountV2InfoUseCase getUserAccountV2InfoUseCase;

  @MockBean
  private UpdateUserAccountV2InfoUseCase updateUserAccountV2InfoUseCase;

  @MockBean
  private SecurityUtils securityUtils;

  @MockBean
  private AccountV2DTOMapper accountV2DTOMapper;

  @MockBean
  private AccountV2InfoUpdateMapper accountV2InfoUpdateMapper;

  @Test
  @SneakyThrows
  void getPersonalUserInfoWithLoggedUserTest() {
    // Given
    Long userId = 1L;
    var accountV2 = getAccountV2();
    var userAccountInfoDTO = getUserAccountInfoDTO();
    when(securityUtils.getAuthenticatedUserId()).thenReturn(userId);
    when(getUserAccountV2InfoUseCase.getUserAccountInfo(userId)).thenReturn(accountV2);
    when(accountV2DTOMapper.toUserAccountInfoDto(accountV2)).thenReturn(userAccountInfoDTO);

    // When
    mockMvc.perform(get("/v2/myInfo")
        .with(getJwtRequest(userId, ROLE_USER)))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().json(objectMapper.writeValueAsString(userAccountInfoDTO)));

    // Then
    verify(securityUtils, times(1)).getAuthenticatedUserId();
    verify(getUserAccountV2InfoUseCase, times(1)).getUserAccountInfo(userId);
    verify(accountV2DTOMapper, times(1)).toUserAccountInfoDto(accountV2);
  }

  @SneakyThrows
  @Test
  void updatePersonalUserInfoWithValidDataTest() {
    // Given
    Long userId = 1L;
    var requestDto = getUpdateAccountV2InfoRequestDTO();
    var updateDto = getUpdateUserAccountV2InfoDto();
    when(securityUtils.getAuthenticatedUserId()).thenReturn(userId);
    when(accountV2InfoUpdateMapper.toUpdateUserAccountV2InfoDto(requestDto)).thenReturn(updateDto);

    // When
    mockMvc.perform(patch("/v2/myInfo")
        .with(getJwtRequest(userId, ROLE_USER))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(requestDto)))
        .andExpect(status().isNoContent());

    // Then
    verify(securityUtils, times(1)).getAuthenticatedUserId();
    verify(accountV2InfoUpdateMapper, times(1)).toUpdateUserAccountV2InfoDto(requestDto);
    verify(updateUserAccountV2InfoUseCase, times(1)).updateUserAccountInfo(userId, updateDto);
  }

  @SneakyThrows
  @Test
  void updatePersonalUserInfoIfUserNotFoundTest() {
    // Given
    Long userId = 1L;
    var requestDto = getUpdateAccountV2InfoRequestDTO();
    var updateDto = getUpdateUserAccountV2InfoDto();
    when(securityUtils.getAuthenticatedUserId()).thenReturn(userId);
    when(accountV2InfoUpdateMapper.toUpdateUserAccountV2InfoDto(requestDto)).thenReturn(updateDto);
    doThrow(new AccountNotFoundException(userId)).when(updateUserAccountV2InfoUseCase).updateUserAccountInfo(userId, updateDto);

    // When
    mockMvc.perform(patch("/v2/myInfo")
        .with(getJwtRequest(userId, ROLE_USER))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(requestDto)))
        .andExpect(status().isNotFound());

    // Then
    verify(securityUtils, times(1)).getAuthenticatedUserId();
    verify(accountV2InfoUpdateMapper, times(1)).toUpdateUserAccountV2InfoDto(requestDto);
    verify(updateUserAccountV2InfoUseCase, times(1)).updateUserAccountInfo(userId, updateDto);
  }
}
