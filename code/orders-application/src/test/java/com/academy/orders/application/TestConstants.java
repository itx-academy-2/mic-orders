package com.academy.orders.application;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class TestConstants {
  public static final String LANGUAGE_UK = "uk";

  public static final String LANGUAGE_EN = "en";

  public static final String IMAGE_URL = "https://example.com/image.jpg";

  public static final String IMAGE_NAME = "image.jpg";

  public static final Long TEST_ID = 1L;

  public static final UUID TEST_UUID = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

  public static final Double PERCENTAGE_OF_TOTAL_ORDERS = 20.8;

  public static final int TEST_QUANTITY = 10;

  public static final int TEST_AMOUNT = 50;

  public static final LocalDateTime TEST_START_DATE = LocalDateTime.of(2020, 1, 1, 1, 1);

  public static final LocalDateTime TEST_END_DATE = LocalDateTime.of(2020, 1, 8, 1, 1);

  public static final String ROLE_ADMIN = "ROLE_ADMIN";

  public static final String ROLE_MANAGER = "ROLE_MANAGER";

  public static final BigDecimal TEST_PRICE = BigDecimal.valueOf(999.99);

  public static final BigDecimal TEST_DISCOUNT_PRICE = BigDecimal.valueOf(899.99);

  public static final String TAG_NAME = "electronics";

  public static final String PRODUCT_NAME = "IPhone";

  public static final String PRODUCT_DESCRIPTION = "Phone";
}
