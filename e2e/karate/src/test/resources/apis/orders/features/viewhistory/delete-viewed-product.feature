Feature: Delete product from viewed history

  Background:
    * url urls.retailApiUrl
    * def authHeader = callonce read('classpath:karate-auth.js')
    * def product = call read('classpath:apis/orders/helpers/getProductId.feature')
    * def productId = product.productId
    * def myViewHistoryPath = '/v1/my-view-history'


  @GS3-57
  Scenario: Add → Delete → Verify it’s removed
    # Add product to viewed history
    Given headers authHeader
    And path myViewHistoryPath, productId
    When method put
    Then status 200

    # Ensure product appears in history
    Given headers authHeader
    And path myViewHistoryPath
    When method get
    Then status 200
    * match response.content[*].id contains productId

    # Delete product from history
    Given headers authHeader
    And path myViewHistoryPath, productId
    When method delete
    Then status 204

    # Ensure it no longer appears in the history
    Given headers authHeader
    And path myViewHistoryPath
    When method get
    Then status 200
    * match response.content[*].id !contains productId
