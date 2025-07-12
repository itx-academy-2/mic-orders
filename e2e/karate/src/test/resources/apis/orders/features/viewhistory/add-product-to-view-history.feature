Feature: Add product to viewed history

  Background:
    * url urls.retailApiUrl
    * def authHeader = callonce read('classpath:karate-auth.js')
    * def product = call read('classpath:apis/orders/helpers/getProductId.feature')
    * def productId = product.productId
    * def myViewHistoryPath = '/v1/my-view-history'

  @GS-57
  Scenario: Add → Verify it appears in viewed history → Clean up
    # Add product to viewed history
    Given headers authHeader
    And path myViewHistoryPath, productId
    When method put
    Then status 200

    # Verify product appears in history
    Given headers authHeader
    And path myViewHistoryPath
    When method get
    Then status 200
    * match response.content[*].id contains productId

    # Remove product from history
    Given headers authHeader
    And path myViewHistoryPath, productId
    When method delete
    Then status 204
