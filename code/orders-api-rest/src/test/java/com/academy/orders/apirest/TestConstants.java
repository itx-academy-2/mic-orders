package com.academy.orders.apirest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class TestConstants {
  public static final String GET_ARTICLE_BY_ID_URL = "/v1/articles/{articleId}";

  public static final String GET_ARTICLES_DETAILS_URL = "/v1/articles/details";

  public static final String SEARCH_ARTICLES_URL = "/v1/articles/search";

  public static final String GET_ALL_PRODUCTS_URL = "/v1/products";

  public static final String SEARCH_PRODUCTS_URL = "/v1/products/search";

  public static final String UPDATE_ORDER_STATUS_URL = "/v1/management/orders/{orderId}/status";

  public static final String UPDATE_PRODUCT_URL = "/v1/management/products/{productId}";

  public static final String GET_PRODUCT_BY_ID_URL = "/v1/management/products/{productId}";

  public static final String GET_PRODUCT_DETAILS_URL = "/v1/products/{productId}";

  public static final String GET_PRODUCTS_ON_SALES_URL = "/v1/products/sales";

  public static final String FIND_BESTSELLERS_URL = "/v1/products/bestsellers";

  public static final String FIND_BESTSELLER_URL = "/v1/products/bestseller";

  public static final String LANGUAGE_UK = "uk";

  public static final String LANGUAGE_EN = "en";

  public static final String IMAGE_URL = "https://example.com/image.jpg";

  public static final Long TEST_ID = 1L;

  public static final UUID TEST_UUID = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

  public static final int TEST_QUANTITY = 10;

  public static final BigDecimal TEST_PRICE = BigDecimal.valueOf(999.99);

  public static final BigDecimal TEST_PRICE_WITH_DISCOUNT = BigDecimal.valueOf(1999.98);

  public static final int TEST_AMOUNT = 10;

  public static final LocalDateTime TEST_START_DATE = LocalDateTime.of(2020, 1, 1, 23, 29);

  public static final LocalDateTime TEST_END_DATE = LocalDateTime.of(2020, 2, 16, 23, 29);

  public static final Float TEST_FLOAT_PRICE = 999.99f;

  public static final String TAG_NAME = "electronics";

  public static final String PRODUCT_NAME = "IPhone";

  public static final String PRODUCT_DESCRIPTION = "Phone";

  public static final Double PERCENTAGE_OF_TOTAL_ORDERS = 25.5;

  public static final String TEST_FIRST_NAME = "mockedFirstName";

  public static final String TEST_LAST_NAME = "mockedLastName";

  public static final String TEST_EMAIL = "mockedmail@mail.com";

  public static final String TEST_CITY = "mockedCity";

  public static final String TEST_DEPARTMENT = "mockedDepartment";

  public static final String ROLE_ADMIN = "ROLE_ADMIN";

  public static final String ROLE_USER = "ROLE_USER";

  public static final String ROLE_MANAGER = "ROLE_MANAGER";

  public static final String TEST_PASSWORD = "123Qyz!";

  public static final int TEST_COUNT = 10;
}
