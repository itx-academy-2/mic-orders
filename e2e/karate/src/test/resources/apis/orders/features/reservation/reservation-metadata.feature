Feature: Get reservations metadata

  Background:
    * url urls.retailApiUrl
    * def credentials = user
    * def authHeader = callonce read('classpath:karate-auth.js') credentials
    * def product = call read('classpath:apis/orders/helpers/getProductId.feature')
    * def productId = product.productId
    * def metadataPath = '/v1/my-reservations/metadata'
    * def metadataSchema = read('classpath:apis/orders/test-data/responses/reservation/userReservationsMetadataSchema.json')
    # Ensure clean state
    * call read('classpath:apis/orders/helpers/reservation/remove-reservation.feature') { productId: '#(productId)' }

  @GS2-282
  Scenario: Get reservations metadata (Add -> Verify -> Remove)

    # --- 1. Get initial metadata ---
    Given headers authHeader
    And path metadataPath
    When method get
    Then status 200
    * match response == metadataSchema
    * def initialRemainingMoney = response.remainingMoney
    * def initialRemainingItems = response.remainingItems
    * def initialReservationCount = response.reservations.length

    # --- 2. Add product ---
    * call read('classpath:apis/orders/helpers/reservation/add-reservation.feature') { productId: '#(productId)' }

    # --- 3. Fetch metadata after add ---
    Given headers authHeader
    And path metadataPath
    When method get
    Then status 200
    * match response == metadataSchema
    * match each response.reservations == { id: "#uuid", reservedAt: "#string" }
    * match response.reservations[*].id contains productId
    * assert response.remainingMoney < initialRemainingMoney
    * assert response.remainingItems < initialRemainingItems
    * assert response.reservations.length == initialReservationCount + 1

    # --- 4. Remove product ---
    * call read('classpath:apis/orders/helpers/reservation/remove-reservation.feature') { productId: '#(productId)' }

    # --- 5. Verify removal ---
    Given headers authHeader
    And path metadataPath
    When method get
    Then status 200
    * match response == metadataSchema
    * match response.reservations[*].id !contains productId