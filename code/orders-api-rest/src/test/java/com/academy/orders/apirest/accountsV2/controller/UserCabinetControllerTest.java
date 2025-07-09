package com.academy.orders.apirest.accountsV2.controller;

import com.academy.orders.apirest.accountsV2.mapper.AccountV2DTOMapper;
import com.academy.orders.apirest.auth.util.SecurityUtils;
import com.academy.orders.domain.accountv2.usecase.GetUserAccountV2InfoUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static com.academy.orders.apirest.ModelUtils.getAccountV2;
import static com.academy.orders.apirest.ModelUtils.getJwtRequest;
import static com.academy.orders.apirest.ModelUtils.getUserAccountInfoDTO;
import static com.academy.orders.apirest.TestConstants.ROLE_USER;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserCabinetController.class)
@ContextConfiguration(classes = UserCabinetController.class)
class UserCabinetControllerTest {
  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private GetUserAccountV2InfoUseCase getUserAccountV2InfoUseCase;

  @MockBean
  private SecurityUtils securityUtils;

  @MockBean
  private AccountV2DTOMapper accountV2DTOMapper;

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
}
