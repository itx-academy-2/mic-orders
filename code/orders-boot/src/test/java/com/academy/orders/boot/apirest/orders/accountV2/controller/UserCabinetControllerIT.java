package com.academy.orders.boot.apirest.orders.accountV2.controller;

import com.academy.orders.boot.apirest.orders.common.AbstractControllerIT;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserCabinetControllerIT extends AbstractControllerIT {
  @Value("${auth.users[0].username}")
  private String user;

  private static final String ENDPOINT_URI = "/v2/myInfo";

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
}
