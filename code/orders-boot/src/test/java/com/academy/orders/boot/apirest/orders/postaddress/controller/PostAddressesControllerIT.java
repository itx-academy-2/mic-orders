package com.academy.orders.boot.apirest.orders.postaddress.controller;

import com.academy.orders.boot.apirest.orders.common.AbstractControllerIT;
import com.academy.orders.domain.postaddress.entity.PostAddressV2;
import com.academy.orders.infrastructure.postaddress.entity.PostAddressV2Entity;
import com.academy.orders_api_rest.generated.model.UserPostAddressResponseDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.simple.JdbcClient;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.Random;
import java.util.stream.Collectors;

import static com.academy.orders.ModelUtils.getPostAddressV2WithPermanentAddressNewData;
import static com.academy.orders.ModelUtils.getPostAddressV2WithTemporaryAddress;
import static com.academy.orders.ModelUtils.getPostAddressV2WithPermanentAddress;
import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PostAddressesControllerIT extends AbstractControllerIT {
  @Value("${auth.users[3].username}")
  private String username;

  @Value("${auth.users[0].username}")
  private String anotherUsername;

  @Value("${auth.users[1].username}")
  private String admin;

  @Autowired
  private JdbcClient jdbcClient;

  private Long accountId;

  private final String ENDPOINT_GET = "/v1/users/%d/addresses";

  private final String ENDPOINT_DELETE = "/v1/users/%d/addresses/%s";

  @AfterEach
  void cleanUp() {
    jdbcClient.sql("DELETE FROM post_addresses_v2 WHERE account_id = :accountId")
        .param("accountId", accountId)
        .update();

    jdbcClient.sql("DELETE FROM accounts WHERE id = :accountId")
        .param("accountId", accountId)
        .update();
  }

  @Test
  void getPermanentPostAddressesByUserId_PermanentAddressesFound_Test() {
    // Given
    insertUserIntoDataBase();
    prepareTestPostAddresses();

    final var url = baseUrl() + format(ENDPOINT_GET, accountId);
    final HttpHeaders headers = buildAuthHeaders(username);
    headers.set("Content-Type", "application/json");

    final var requestEntity = new HttpEntity<>(headers);

    // When
    final var response =
        restTemplate.exchange(url, HttpMethod.GET, requestEntity, new ParameterizedTypeReference<List<UserPostAddressResponseDTO>>() {});

    // Then
    assertEquals(200, response.getStatusCode().value());

    List<UserPostAddressResponseDTO> body = response.getBody();
    assertNotNull(body);
    assertEquals(2, body.size(), "Only permanent addresses should be returned");

    Set<String> titles = body.stream()
        .map(UserPostAddressResponseDTO::getTitle)
        .collect(Collectors.toSet());

    assertEquals(Set.of("Friend", "Family"), titles);
  }

  @Test
  void getPermanentPostAddressesByUserId_PermanentAddressesNotFound_Test() {
    // Given
    insertUserIntoDataBase();
    final var url = baseUrl() + format(ENDPOINT_GET, accountId);
    final HttpHeaders headers = buildAuthHeaders(username);
    headers.set("Content-Type", "application/json");

    final var requestEntity = new HttpEntity<>(headers);

    // When
    final var response = restTemplate.exchange(
        url,
        HttpMethod.GET,
        requestEntity,
        new ParameterizedTypeReference<List<UserPostAddressResponseDTO>>() {});

    // Then
    assertEquals(200, response.getStatusCode().value());
    List<UserPostAddressResponseDTO> body = response.getBody();
    assertNotNull(body);
    assertEquals(0, body.size());
  }

  @Test
  void getPermanentPostAddressesByUserId_ForbiddenAccess_Test() {
    // Given
    insertUserIntoDataBase();
    prepareTestPostAddresses();

    final var url = baseUrl() + format(ENDPOINT_GET, accountId);
    final HttpHeaders headers = buildAuthHeaders(anotherUsername);
    headers.set("Content-Type", "application/json");

    final var requestEntity = new HttpEntity<>(headers);

    // When
    final var response = restTemplate.exchange(
        url,
        HttpMethod.GET,
        requestEntity,
        String.class);

    // Then
    assertEquals(403, response.getStatusCode().value());
  }

  @Test
  void getPermanentPostAddressesByUserId_UnauthorizedAccess_Test() {
    // Given
    insertUserIntoDataBase();
    prepareTestPostAddresses();

    final var url = baseUrl() + format(ENDPOINT_GET, accountId);
    final HttpHeaders headers = new HttpHeaders();
    headers.set("Content-Type", "application/json");

    final var requestEntity = new HttpEntity<>(headers);

    // When
    final var response = restTemplate.exchange(
        url,
        HttpMethod.GET,
        requestEntity,
        String.class);

    // Then
    assertEquals(401, response.getStatusCode().value());
  }

  private void prepareTestPostAddresses() {
    insertAddressIntoDataBase(getPostAddressV2WithPermanentAddress());
    insertAddressIntoDataBase(getPostAddressV2WithPermanentAddressNewData());
    insertAddressIntoDataBase(getPostAddressV2WithTemporaryAddress());
  }

  private void insertAddressIntoDataBase(PostAddressV2 address) {
    jdbcClient.sql("""
            INSERT INTO post_addresses_v2 (id, city, department, delivery_method, recipient_first_name,
                                        recipient_last_name, recipient_phone, title, account_id)
            VALUES (:id, :city, :department, :deliveryMethod, :firstName, :lastName, :phone, :title, :accountId)
        """)
        .param("city", address.city())
        .param("department", address.department())
        .param("deliveryMethod", address.deliveryMethod().name())
        .param("firstName", address.recipientFirstName())
        .param("lastName", address.recipientLastName())
        .param("phone", address.recipientPhone())
        .param("title", address.title())
        .param("accountId", accountId)
        .param("id", UUID.randomUUID())
        .update();
  }

  private void insertUserIntoDataBase() {
    jdbcClient.sql("""
        INSERT INTO accounts (email, password, first_name, last_name, role, status, created_at)
        SELECT :email, :password, :firstName, :lastName, :role, :status, now()
        WHERE NOT EXISTS (
            SELECT 1 FROM accounts WHERE email = :email
        )
        """)
        .param("email", "user-2@mail.com")
        .param("password", "$2y$10$6IQper1XrKudSKuC8J0hnO1hJGsfexYdee6mgy2Lh.amnufF5/2cu")
        .param("firstName", "user")
        .param("lastName", "user")
        .param("role", "ROLE_USER")
        .param("status", "ACTIVE")
        .update();

    accountId = jdbcClient.sql("SELECT id FROM accounts WHERE email = :email")
        .param("email", "user-2@mail.com")
        .query(Long.class)
        .single();
  }

  @Test
  void removeUserPermanentAddress_Success_Test() {
    // Given
    insertUserIntoDataBase();
    prepareTestPostAddresses();

    var listOfUserPostAddresses = getUserPostAddresses(accountId);
    var permanentAddressIdForTest =
        listOfUserPostAddresses.stream().filter(e -> e.getTitle().startsWith("permanent: ")).findFirst().orElseThrow().getId();

    final var url = baseUrl() + format(ENDPOINT_DELETE, accountId, permanentAddressIdForTest);
    final HttpHeaders headers = buildAuthHeaders(username);
    headers.setAccept(List.of(MediaType.APPLICATION_JSON));

    final var requestEntity = new HttpEntity<>(headers);

    // When
    final var response =
        restTemplate.exchange(url, HttpMethod.DELETE, requestEntity, Void.class);

    // Then
    assertEquals(204, response.getStatusCode().value());

    var updatedEntity = getPostAddressV2EntityById(permanentAddressIdForTest).orElseThrow();
    assertTrue(updatedEntity.getTitle().startsWith("temp: "), "Title should start with 'temp: ' after removing the permanent address");
  }

  @Test
  void removeUserPermanentAddress_DoesntChangeAlreadyTemporaryTitle_Test() {
    // Given
    insertUserIntoDataBase();
    prepareTestPostAddresses();

    var listOfUserPostAddresses = getUserPostAddresses(accountId);
    var originalPostAddressWithTemporaryTitle =
        listOfUserPostAddresses.stream().filter(e -> e.getTitle().startsWith("temp: ")).findFirst().orElseThrow();
    var temporaryAddressIdForTest = originalPostAddressWithTemporaryTitle.getId();

    final var url = baseUrl() + format(ENDPOINT_DELETE, accountId, temporaryAddressIdForTest);
    final HttpHeaders headers = buildAuthHeaders(username);
    headers.setAccept(List.of(MediaType.APPLICATION_JSON));

    final var requestEntity = new HttpEntity<>(headers);

    // When
    final var response =
        restTemplate.exchange(url, HttpMethod.DELETE, requestEntity, Void.class);

    // Then
    assertEquals(204, response.getStatusCode().value());

    var entityAfterUpdateAttempt = getPostAddressV2EntityById(temporaryAddressIdForTest).orElseThrow();
    assertEquals(entityAfterUpdateAttempt.getTitle(), originalPostAddressWithTemporaryTitle.getTitle());
  }

  @Test
  void removeUserPermanentAddress_AccountNotFound_Test() {
    // Given
    Long nonexistentAccountId = 88L;

    final var url = baseUrl() + format(ENDPOINT_DELETE, nonexistentAccountId, UUID.randomUUID());
    final HttpHeaders headers = buildAuthHeaders(admin);
    headers.setAccept(List.of(MediaType.APPLICATION_JSON));

    final var requestEntity = new HttpEntity<>(headers);

    // When
    final var response =
        restTemplate.exchange(url, HttpMethod.DELETE, requestEntity, String.class);

    // Then
    assertEquals(404, response.getStatusCode().value());
    assertNotNull(response.getBody());
    assertTrue(response.getBody().contains("Account with id: 88 is not found"));
  }

  @Test
  void removeUserPermanentAddress_PostAddressNotFound_Test() {
    // Given
    insertUserIntoDataBase();
    UUID nonexistentPostAddressId = UUID.randomUUID();

    final var url = baseUrl() + format(ENDPOINT_DELETE, accountId, nonexistentPostAddressId);
    final HttpHeaders headers = buildAuthHeaders(username);
    headers.setAccept(List.of(MediaType.APPLICATION_JSON));

    final var requestEntity = new HttpEntity<>(headers);

    // When
    final var response =
        restTemplate.exchange(url, HttpMethod.DELETE, requestEntity, String.class);

    // Then
    assertEquals(404, response.getStatusCode().value());
    assertNotNull(response.getBody());
    assertTrue(response.getBody().contains(format("PostAddress with id: %s is not found", nonexistentPostAddressId)));
  }

  @Test
  void removeUserPermanentAddress_ForbiddenAccess_Test() {
    // Given
    insertUserIntoDataBase();
    final var url = baseUrl() + format(ENDPOINT_DELETE, accountId, UUID.randomUUID());
    final HttpHeaders headers = buildAuthHeaders(anotherUsername);
    headers.setAccept(List.of(MediaType.APPLICATION_JSON));

    final var requestEntity = new HttpEntity<>(headers);

    // When
    final var response =
        restTemplate.exchange(url, HttpMethod.DELETE, requestEntity, Void.class);

    // Then
    assertEquals(403, response.getStatusCode().value());
  }

  @Test
  void removeUserPermanentAddress_Unauthorised_Test() {
    // Given
    var randomAccountId = Math.abs(new Random().nextLong());
    final var url = baseUrl() + format(ENDPOINT_DELETE, randomAccountId, UUID.randomUUID());
    final HttpHeaders headers = new HttpHeaders();
    headers.setAccept(List.of(MediaType.APPLICATION_JSON));

    final var requestEntity = new HttpEntity<>(headers);

    // When
    final var response =
        restTemplate.exchange(url, HttpMethod.DELETE, requestEntity, Void.class);

    // Then
    assertEquals(401, response.getStatusCode().value());
  }

  private List<PostAddressV2Entity> getUserPostAddresses(Long userId) {
    return jdbcClient.sql("""
        SELECT * FROM post_addresses_v2 WHERE account_id = :userId
        """)
        .param("userId", userId)
        .query(PostAddressV2Entity.class)
        .list();
  }

  private Optional<PostAddressV2Entity> getPostAddressV2EntityById(UUID addressId) {
    return jdbcClient.sql("""
        SELECT * FROM post_addresses_v2 WHERE id = :addressId
        """)
        .param("addressId", addressId)
        .query(PostAddressV2Entity.class)
        .optional();
  }
}
