package com.academy.orders.boot.apirest.orders.orderV2.controller;

import com.academy.orders.boot.apirest.orders.common.AbstractControllerIT;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Map;
import java.util.UUID;

import static com.academy.orders.ModelUtils.getPlaceOrderRequestV2DTO;
import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class OrdersV2ControllerIT extends AbstractControllerIT {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private Long accountId;

    private UUID createdOrderId;

    private final String ENDPOINT = "/v2/users/%d/orders";

    @BeforeEach
    void setUp() {
        jdbcTemplate.update("""
        INSERT INTO accounts (email, password, first_name, last_name, role, status, created_at)
        SELECT ?, ?, ?, ?, ?, ?, now()
        WHERE NOT EXISTS (
            SELECT 1 FROM accounts WHERE email = ?
        )
        """,
                "user-for-orders-v2@mail.com",
                "$2y$10$6IQper1XrKudSKuC8J0hnO1hJGsfexYdee6mgy2Lh.amnufF5/2cu",
                "user", "user", "ROLE_USER", "ACTIVE",
                "user-for-orders-v2@mail.com"
        );

        accountId = jdbcTemplate.queryForObject(
                "SELECT id FROM accounts WHERE email = ?", new Object[] {"user-for-orders-v2@mail.com"}, Long.class);

        jdbcTemplate.update("""
        INSERT INTO cart_items (product_id, user_id, quantity)
        VALUES 
          ('f4831fef-35a8-4766-b50e-dcb25d7b2e7b', ?, 1),
          ('e404a9ae-0c40-4015-8574-9a9873793134', ?, 3),
          ('8551c666-423e-44d6-a317-ca019d91b219', ?, 1)
        """, accountId, accountId, accountId);
    }

    @AfterEach
    void cleanup() {
        jdbcTemplate.update("DELETE FROM cart_items WHERE user_id = ?", accountId);

        if (createdOrderId != null) {
            jdbcTemplate.update("DELETE FROM order_items WHERE order_id = ?", createdOrderId);
            jdbcTemplate.update("DELETE FROM post_addresses WHERE id = ?", createdOrderId);
            jdbcTemplate.update("DELETE FROM orders WHERE id = ?", createdOrderId);
            jdbcTemplate.update("DELETE FROM order_items_v2 WHERE order_v2_id = ?", createdOrderId);
            jdbcTemplate.update("DELETE FROM orders_v2 WHERE id = ?", createdOrderId);
            jdbcTemplate.update("DELETE FROM post_addresses_v2 WHERE account_id = ?", accountId);
        }
    }

    @Test
    void placeOrderV2_Success_Test() {
        //Given
        final var username = "user-for-orders-v2@mail.com";
        final var url = baseUrl() + format(ENDPOINT, accountId);
        final HttpHeaders headers = buildAuthHeaders(username);
        headers.set("Content-Type", "application/json");
        var requestBody = getPlaceOrderRequestV2DTO();

        final var requestEntity = new HttpEntity<>(requestBody, headers);

        //When
        final var response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, Map.class);

        //Then
        assertEquals(201, response.getStatusCode().value());

        var body = response.getBody();
        if (body != null && body.get("orderId") != null) {
            createdOrderId = UUID.fromString(body.get("orderId").toString());
        }
    }

    @Test
    void placeOrderV2_Conflict_Test() {
        //Given
        final var username = "user-for-orders-v2@mail.com";
        final var url = baseUrl() + format(ENDPOINT, accountId);
        final HttpHeaders headers = buildAuthHeaders(username);
        headers.set("Content-Type", "application/json");
        var requestBody = getPlaceOrderRequestV2DTO();

        final var requestEntity = new HttpEntity<>(requestBody, headers);

        //When
        final var firstOrder = restTemplate.exchange(url, HttpMethod.POST, requestEntity, Map.class);

        //And
        requestBody.setFirstName("Olga");

        jdbcTemplate.update("""
        INSERT INTO cart_items (product_id, user_id, quantity)
        VALUES 
          ('f4831fef-35a8-4766-b50e-dcb25d7b2e7b', ?, 1)
        """, accountId);

        //When
        final var response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, Map.class);

        //Then
        assertEquals(409, response.getStatusCode().value());

        var body = firstOrder.getBody();
        if (body != null && body.get("orderId") != null) {
            createdOrderId = UUID.fromString(body.get("orderId").toString());
        }
    }

    @Test
    void placeOrderV2_Forbidden_Test() {
        //Given
        final var username = "user-for-orders-v2@mail.com";
        final var url = baseUrl() + format(ENDPOINT, 2L);
        final HttpHeaders headers = buildAuthHeaders(username);
        headers.set("Content-Type", "application/json");
        var requestBody = getPlaceOrderRequestV2DTO();

        final var requestEntity = new HttpEntity<>(requestBody, headers);

        //When
        final var response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, Map.class);

        //Then
        assertEquals(403, response.getStatusCode().value());

        var body = response.getBody();
        if (body != null && body.get("orderId") != null) {
            createdOrderId = UUID.fromString(body.get("orderId").toString());
        }
    }

    @Test
    void placeOrderV2_Unauthorized_Test() {
        //Given
        final var url = baseUrl() + format(ENDPOINT, 67L);
        final HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.setBearerAuth("invalid-token");

        var requestBody = getPlaceOrderRequestV2DTO();

        final var requestEntity = new HttpEntity<>(requestBody, headers);

        //When
        final var response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, Map.class);

        //Then
        assertEquals(401, response.getStatusCode().value());

        var body = response.getBody();
        if (body != null && body.get("orderId") != null) {
            createdOrderId = UUID.fromString(body.get("orderId").toString());
        }
    }

    @Test
    void placeOrderV2_BadRequest_Test() {
        //Given
        final var username = "user-for-orders-v2@mail.com";
        final var url = baseUrl() + format(ENDPOINT, accountId);
        final HttpHeaders headers = buildAuthHeaders(username);
        headers.set("Content-Type", "application/json");
        var requestBody = getPlaceOrderRequestV2DTO();

        final var requestEntity = new HttpEntity<>(requestBody, headers);

        //And
        jdbcTemplate.update("DELETE FROM cart_items WHERE user_id = ?", accountId);

        //When
        final var response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, Map.class);

        //Then
        assertEquals(400, response.getStatusCode().value());

        var body = response.getBody();
        if (body != null && body.get("orderId") != null) {
            createdOrderId = UUID.fromString(body.get("orderId").toString());
        }
    }
}
