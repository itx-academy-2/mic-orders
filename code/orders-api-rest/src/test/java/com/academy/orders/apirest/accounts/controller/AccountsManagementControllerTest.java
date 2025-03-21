package com.academy.orders.apirest.accounts.controller;

import com.academy.orders.apirest.accounts.mapper.AccountDTOMapper;
import com.academy.orders.apirest.accounts.mapper.AccountResponseDTOMapper;
import com.academy.orders.apirest.common.ErrorHandler;
import com.academy.orders.apirest.common.TestSecurityConfig;
import com.academy.orders.domain.account.entity.enumerated.UserStatus;
import com.academy.orders.domain.account.exception.AccountNotFoundException;
import com.academy.orders.domain.account.usecase.ChangeAccountStatusUseCase;
import com.academy.orders.domain.account.usecase.GetAllUsersUseCase;
import com.academy.orders_api_rest.generated.model.AccountFilterDTO;
import com.academy.orders_api_rest.generated.model.AccountStatusDTO;
import com.academy.orders_api_rest.generated.model.PageableDTO;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.aop.AopAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static com.academy.orders.apirest.ModelUtils.getAccountManagementFilterDto;
import static com.academy.orders.apirest.ModelUtils.getAccountPage;
import static com.academy.orders.apirest.ModelUtils.getPageAccountsDTO;
import static com.academy.orders.apirest.ModelUtils.getPageable;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AccountsManagementController.class)
@ContextConfiguration(classes = {AccountsManagementController.class})
@Import(value = {AopAutoConfiguration.class, TestSecurityConfig.class, ErrorHandler.class})
class AccountsManagementControllerTest {
  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private AccountDTOMapper accountDTOMapper;

  @MockBean
  private ChangeAccountStatusUseCase changeAccountStatusUseCase;

  @MockBean
  private GetAllUsersUseCase getAllUsersUseCase;

  @MockBean
  private AccountResponseDTOMapper accountResponseDTOMapper;

  @Test
  @WithMockUser(authorities = "ROLE_ADMIN")
  @SneakyThrows
  void changeAccountStatusNotFoundTest() {
    Long id = 1L;
    UserStatus status = UserStatus.ACTIVE;

    when(accountDTOMapper.mapToUserStatus(any(AccountStatusDTO.class))).thenReturn(status);
    doThrow(AccountNotFoundException.class).when(changeAccountStatusUseCase).changeStatus(id, status);

    mockMvc.perform(patch("/v1/management/users/{userId}/status", id).queryParam("status", status.name()))
        .andExpect(status().isNotFound());

    verify(accountDTOMapper).mapToUserStatus(any(AccountStatusDTO.class));
    verify(changeAccountStatusUseCase).changeStatus(id, status);
  }

  @Test
  @WithMockUser(authorities = "ROLE_ADMIN")
  @SneakyThrows
  void changeAccountStatusTest() {
    Long id = 1L;
    UserStatus status = UserStatus.ACTIVE;

    when(accountDTOMapper.mapToUserStatus(any(AccountStatusDTO.class))).thenReturn(status);
    doNothing().when(changeAccountStatusUseCase).changeStatus(id, status);

    mockMvc.perform(patch("/v1/management/users/{userId}/status", id).queryParam("status", status.name()))
        .andExpect(status().isNoContent());

    verify(accountDTOMapper).mapToUserStatus(any(AccountStatusDTO.class));
    verify(changeAccountStatusUseCase).changeStatus(id, status);
  }

  @Test
  @WithMockUser(authorities = "ROLE_ADMIN")
  @SneakyThrows
  void getAccountsTest() {
    var filterDto = getAccountManagementFilterDto();
    var pageable = getPageable();
    var accountPage = getAccountPage();
    var pageAccountsDTO = getPageAccountsDTO();

    when(accountDTOMapper.toDomain(any(AccountFilterDTO.class))).thenReturn(filterDto);
    when(accountDTOMapper.toDomain(any(PageableDTO.class))).thenReturn(pageable);
    when(getAllUsersUseCase.getAllUsers(filterDto, pageable)).thenReturn(accountPage);
    when(accountResponseDTOMapper.toResponse(accountPage)).thenReturn(pageAccountsDTO);

    var result = mockMvc.perform(get("/v1/management/users").queryParam("page", "0").queryParam("size", "5")
        .queryParam("sort", "createdAt,desc")).andExpect(status().isOk()).andReturn();

    String responseBody = result.getResponse().getContentAsString();
    assertThat(responseBody).isNotNull().contains("\"totalElements\":0").contains("\"totalPages\":0")
        .contains("\"first\":true").contains("\"last\":true").contains("\"number\":0")
        .contains("\"numberOfElements\":0").contains("\"size\":5").contains("\"empty\":true")
        .contains("\"content\":[]");

    verify(accountDTOMapper).toDomain(any(AccountFilterDTO.class));
    verify(accountDTOMapper).toDomain(any(PageableDTO.class));
    verify(getAllUsersUseCase).getAllUsers(filterDto, pageable);
    verify(accountResponseDTOMapper).toResponse(accountPage);
  }
}
