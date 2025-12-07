Feature: Manager can view user reservations for a product

  Background:
    * url urls.retailApiUrl
    # Manager authentication
    * def managerAuthResult = call read('classpath:karate-auth.feature@fetchAuthToken') manager
    * def managerToken = managerAuthResult.response.token
    * def managerAuth = { Authorization: '#("Bearer " + managerToken)' }
    # User authentication
    * def userAuthResult = call read('classpath:karate-auth.feature@fetchAuthToken') user
    * def userToken = userAuthResult.response.token
    * def userAuth = { Authorization: '#("Bearer " + userToken)' }
    # Test data
    * def product = call read('classpath:apis/orders/helpers/getProductId.feature')
    * def productId = product.productId
    * def userEmail = user.username
    # Paths
    * def managerReservationsPath = '/v1/management/products/' + productId + '/reservations'
    # Schemas
    * def pageSchema = read('classpath:apis/orders/test-data/responses/common/pageSchema.json')
    * def reservationSchema = read('classpath:apis/orders/test-data/responses/reservation/reservationDetailsSchema.json')
    # Cleanup hook - ensures reservation is removed even if test fails
    * configure afterScenario = function(){ karate.call('classpath:apis/orders/helpers/reservation/remove-reservation.feature', { productId: productId }) }

  Scenario: User adds reservation → Manager verifies it appears → User removes it
    # 1. User adds product to reservations
    * call read('classpath:apis/orders/helpers/reservation/add-reservation.feature') { productId: '#(productId)' }

    # 2. Manager verifies reservation appears in the list
    Given headers managerAuth
    And path managerReservationsPath
    And param page = 0
    And param size = 10
    When method get
    Then status 200
    * match response contains pageSchema
    * match each response.content contains reservationSchema
    * match response.content[*].email contains userEmail

    # 3. User removes reservation
    * call read('classpath:apis/orders/helpers/reservation/remove-reservation.feature') { productId: '#(productId)' }

    # 4. Verify reservation is removed from user's list
    * def userReservations = call read('classpath:apis/orders/helpers/reservation/get-user-reservations.feature')
    * match userReservations.reservations[*].id !contains productId

  Scenario: Unauthenticated user gets 401 when accessing manager reservations endpoint
    Given path managerReservationsPath
    And param page = 0
    And param size = 10
    When method get
    Then status 401

  Scenario: Regular user gets 403 when accessing manager reservations endpoint
    Given headers userAuth
    And path managerReservationsPath
    And param page = 0
    And param size = 10
    When method get
    Then status 403

  Scenario: Manager gets 404 for non-existent product
    * def nonExistentProductId = '00000000-0000-0000-0000-000000000000'
    * def nonExistentPath = '/v1/management/products/' + nonExistentProductId + '/reservations'
    Given headers managerAuth
    And path nonExistentPath
    And param page = 0
    And param size = 10
    When method get
    Then status 404
