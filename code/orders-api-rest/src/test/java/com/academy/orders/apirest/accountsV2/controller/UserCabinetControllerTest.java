package com.academy.orders.apirest.accountsV2.controller;

import com.academy.orders.apirest.accountsV2.mapper.AccountV2DTOMapper;
import com.academy.orders.apirest.accountsV2.mapper.AccountV2InfoUpdateMapper;
import com.academy.orders.apirest.auth.util.SecurityUtils;
import com.academy.orders.apirest.common.ErrorHandler;
import com.academy.orders.domain.account.exception.AccountNotFoundException;
import com.academy.orders.domain.accountv2.usecase.DeleteUserProfilePhotoUseCase;
import com.academy.orders.domain.accountv2.usecase.GetUserAccountV2InfoUseCase;
import com.academy.orders.domain.accountv2.usecase.GetUserProfilePhotoUseCase;
import com.academy.orders.domain.accountv2.usecase.UpdateUserAccountV2InfoUseCase;
import com.academy.orders.domain.accountv2.usecase.UpdateUserProfilePhotoUseCase;
import com.academy.orders_api_rest.generated.model.UpdateUserPhotoRequestDTO;
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
import java.net.URI;

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserCabinetController.class)
@ContextConfiguration(classes = UserCabinetController.class)
@Import(ErrorHandler.class)
class UserCabinetControllerTest {
  private static final String MY_INFO_URI = "/v2/my-info";

  private static final String PHOTO_URI = "/v1/my-info/photo";

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private GetUserAccountV2InfoUseCase getUserAccountV2InfoUseCase;

  @MockBean
  private UpdateUserAccountV2InfoUseCase updateUserAccountV2InfoUseCase;

  @MockBean
  private GetUserProfilePhotoUseCase getUserProfilePhotoUseCase;

  @MockBean
  private UpdateUserProfilePhotoUseCase updateUserProfilePhotoUseCase;

  @MockBean
  private DeleteUserProfilePhotoUseCase deleteUserProfilePhotoUseCase;

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
    mockMvc.perform(get(MY_INFO_URI)
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
    mockMvc.perform(patch(MY_INFO_URI)
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
    mockMvc.perform(patch(MY_INFO_URI)
        .with(getJwtRequest(userId, ROLE_USER))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(requestDto)))
        .andExpect(status().isNotFound());

    // Then
    verify(securityUtils, times(1)).getAuthenticatedUserId();
    verify(accountV2InfoUpdateMapper, times(1)).toUpdateUserAccountV2InfoDto(requestDto);
    verify(updateUserAccountV2InfoUseCase, times(1)).updateUserAccountInfo(userId, updateDto);
  }

  @Test
  @SneakyThrows
  void getUserPhotoReturnsPhotoUrlTest() {
    // Given
    Long userId = 2L;
    String photoUrl = "https://example.com/photo.jpg";
    when(securityUtils.getAuthenticatedUserId()).thenReturn(userId);
    when(getUserProfilePhotoUseCase.getProfilePhoto(userId)).thenReturn(photoUrl);

    // When
    mockMvc.perform(get(PHOTO_URI)
        .with(getJwtRequest(userId, ROLE_USER)))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.photo").value(photoUrl));

    // Then
    verify(securityUtils, times(1)).getAuthenticatedUserId();
    verify(getUserProfilePhotoUseCase, times(1)).getProfilePhoto(userId);
  }

  @Test
  @SneakyThrows
  void getUserPhotoReturnsNullPhotoTest() {
    // Given
    Long userId = 2L;
    when(securityUtils.getAuthenticatedUserId()).thenReturn(userId);
    when(getUserProfilePhotoUseCase.getProfilePhoto(userId)).thenReturn(null);

    // When
    mockMvc.perform(get(PHOTO_URI)
        .with(getJwtRequest(userId, ROLE_USER)))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.photo").value((String) null));

    // Then
    verify(securityUtils, times(1)).getAuthenticatedUserId();
    verify(getUserProfilePhotoUseCase, times(1)).getProfilePhoto(userId);
  }

  @Test
  @SneakyThrows
  void updateUserPhotoWithValidUrlTest() {
    // Given
    Long userId = 2L;
    String photoUrl = "https://example.com/new-photo.jpg";
    var requestDTO = new UpdateUserPhotoRequestDTO().photo(URI.create(photoUrl));
    when(securityUtils.getAuthenticatedUserId()).thenReturn(userId);

    // When
    mockMvc.perform(put(PHOTO_URI)
        .with(getJwtRequest(userId, ROLE_USER))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(requestDTO)))
        .andExpect(status().isNoContent());

    // Then
    verify(securityUtils, times(1)).getAuthenticatedUserId();
    verify(updateUserProfilePhotoUseCase, times(1)).updateProfilePhoto(userId, photoUrl);
  }

  @Test
  @SneakyThrows
  void deleteUserPhotoReturnsNoContentTest() {
    // Given
    Long userId = 2L;
    when(securityUtils.getAuthenticatedUserId()).thenReturn(userId);

    // When
    mockMvc.perform(delete(PHOTO_URI)
        .with(getJwtRequest(userId, ROLE_USER)))
        .andExpect(status().isNoContent());

    // Then
    verify(securityUtils, times(1)).getAuthenticatedUserId();
    verify(deleteUserProfilePhotoUseCase, times(1)).deleteProfilePhoto(userId);
  }

  @Test
  @SneakyThrows
  void updateUserPhotoWithNullUrlTest() {
    // Given
    Long userId = 2L;
    var requestDTO = new UpdateUserPhotoRequestDTO().photo(null);
    when(securityUtils.getAuthenticatedUserId()).thenReturn(userId);

    // When
    mockMvc.perform(put(PHOTO_URI)
        .with(getJwtRequest(userId, ROLE_USER))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(requestDTO)))
        .andExpect(status().isBadRequest());

    // Then
    verify(securityUtils, times(0)).getAuthenticatedUserId();
    verify(updateUserProfilePhotoUseCase, times(0)).updateProfilePhoto(userId, null);
  }
}
