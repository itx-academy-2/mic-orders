Feature: Reservation quantity flow with metadata validation

  Background:
    * url urls.retailApiUrl
    * def credentials = user
    * def authHeader = callonce read('classpath:karate-auth.js') credentials
    * def product = call read('classpath:apis/orders/helpers/getProductId.feature')
    * def productId = product.productId
    * def reservationsPath = '/v1/my-reservations'
    * def metadataPath = '/v1/my-reservations/metadata'

  @GS2-288
  Scenario: Add twice → decrease → verify quantity via metadata

    # Add first time
    Given headers authHeader
    And path reservationsPath, productId
    When method put
    Then status 204

    # Add second time
    Given headers authHeader
    And path reservationsPath, productId
    When method put
    Then status 204

    # Verify quantity = 2
    Given headers authHeader
    And path metadataPath
    When method get
    Then status 200
    * def reservation = response.reservations.find(r => r.id == productId)
    * match reservation.reservedQuantity == 2

    # Decrease → quantity should be 1
    Given headers authHeader
    And path reservationsPath, productId, 'decrement'
    When method patch
    Then status 204

    Given headers authHeader
    And path metadataPath
    When method get
    Then status 200
    * def reservation = response.reservations.find(r => r.id == productId)
    * match reservation.reservedQuantity == 1

    # Decrease again → should be removed
    Given headers authHeader
    And path reservationsPath, productId, 'decrement'
    When method patch
    Then status 204

    Given headers authHeader
    And path metadataPath
    When method get
    Then status 200
    * def reservation = response.reservations.find(r => r.id == productId)
    * match reservation == null