package com.academy.orders.boot.apirest.orders.accountV2.controller;

import com.academy.orders.boot.apirest.orders.common.AbstractControllerIT;
import com.academy.orders_api_rest.generated.model.UpdateAccountV2InfoRequestDTO;
import com.academy.orders_api_rest.generated.model.UserAccountInfoDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import java.util.Map;

import static com.academy.orders.ModelUtils.getUpdateAccountV2InfoRequestDTO;
import static org.junit.jupiter.api.Assertions.assertEquals;

class UserCabinetControllerIT extends AbstractControllerIT {
  @Value("${auth.users[0].username}")
  private String user;

  private static final String ENDPOINT_URI = "/v2/my-info";

  @Test
  void getPersonalUserInfoWithLoggedUserTest() {
    // Given
    final var url = baseUrl() + ENDPOINT_URI;
    final HttpHeaders headers = buildAuthHeaders(user);
    final var requestEntity = new HttpEntity<>(headers);

    // When
    final var result = this.restTemplate.exchange(url, HttpMethod.GET, requestEntity, Object.class);

    // Then
    assertEquals(200, result.getStatusCode().value());
  }

  @Test
  void getPersonalUserInfoWithNotLoggedUserTest() {
    // Given
    final var url = baseUrl() + ENDPOINT_URI;
    HttpEntity<Void> requestEntity = new HttpEntity<>(null);

    // WHen
    final var result = this.restTemplate.exchange(url, HttpMethod.GET, requestEntity, Object.class);

    // Then
    assertEquals(401, result.getStatusCode().value());
  }

  @Test
  void updatePersonalUserInfoWithLoggedUserTest() {
    // Given
    final HttpHeaders headers = buildAuthHeaders(user);
    final var url = baseUrl() + ENDPOINT_URI;
    UserAccountInfoDTO originalData = fetchUserInfo(url, headers);

    // When
    UpdateAccountV2InfoRequestDTO newData = getUpdateAccountV2InfoRequestDTO();
    final var result = this.restTemplate.exchange(url, HttpMethod.PATCH, new HttpEntity<>(newData, headers), Void.class);

    // Then
    assertEquals(204, result.getStatusCode().value());
    // Restore Original Data
    final var restoredResult = this.restTemplate.exchange(url, HttpMethod.PATCH,
        new HttpEntity<>(mapToUpdateAccountV2InfoRequestDTO(originalData), headers), Void.class);
    assertEquals(204, restoredResult.getStatusCode().value());
  }

  @Test
  void updatePersonalUserInfoWithNotLoggedUserTest() {
    // Given
    final var url = baseUrl() + ENDPOINT_URI;
    final var requestBody = getUpdateAccountV2InfoRequestDTO();
    final HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    final var requestEntity = new HttpEntity<>(requestBody, headers);

    // When
    final var result = this.restTemplate.exchange(url, HttpMethod.PATCH, requestEntity, Object.class);

    // Then
    assertEquals(401, result.getStatusCode().value());
  }

  @Test
  void updatePersonalUserInfoWithInvalidRequestTest() {
    // Given
    final var url = baseUrl() + ENDPOINT_URI;
    final HttpHeaders headers = buildAuthHeaders(user);
    headers.setContentType(MediaType.APPLICATION_JSON);

    final var invalidRequestBody = Map.of(
        "firstName", "",
        "lastName", "",
        "phone", "+38063");

    final var requestEntity = new HttpEntity<>(invalidRequestBody, headers);

    // When
    final var result = this.restTemplate.exchange(url, HttpMethod.PATCH, requestEntity, Object.class);

    // Then
    assertEquals(400, result.getStatusCode().value());
  }

  private UserAccountInfoDTO fetchUserInfo(String url, HttpHeaders headers) {
    var response = this.restTemplate.exchange(
        url, HttpMethod.GET, new HttpEntity<>(headers), UserAccountInfoDTO.class);
    assertEquals(200, response.getStatusCode().value());
    return response.getBody();
  }

  private UpdateAccountV2InfoRequestDTO mapToUpdateAccountV2InfoRequestDTO(UserAccountInfoDTO info) {
    var dto = new UpdateAccountV2InfoRequestDTO();
    dto.setFirstName(info.getFirstName());
    dto.setLastName(info.getLastName());
    dto.setPhone(info.getPhone());
    return dto;
  }
}
