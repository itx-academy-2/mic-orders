Feature: Add product to reservations → Verify it appears → Remove it

  Background:
    * url urls.retailApiUrl
    * def credentials = user
    * def authHeader = callonce read('classpath:karate-auth.js') credentials
    * def product = call read('classpath:apis/orders/helpers/getProductId.feature')
    * def productId = product.productId
    * def reservationsPath = '/v1/my-reservations'

  @GS2-217
  Scenario: Add product to reservations → Verify it appears → Remove it
    # Add to reservations
    Given headers authHeader
    And path reservationsPath, productId
    When method put
    Then status 204

    # Verify it appears in reservations
    Given headers authHeader
    And path reservationsPath
    And param lang = 'en'
    When method get
    Then status 200
    * match response[*].id contains productId

    # Remove from reservations
    Given headers authHeader
    And path reservationsPath, productId
    When method delete
    Then status 204

    # Verify removal
    Given headers authHeader
    And path reservationsPath
    And param lang = 'en'
    When method get
    Then status 200
    * match response[*].id !contains productId
