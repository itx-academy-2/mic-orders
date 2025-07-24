Feature: Add product to wishlist → Verify it appears → Remove it

  Background:
    * url urls.retailApiUrl
    * def authHeader = callonce read('classpath:karate-auth.js')
    * def product = call read('classpath:apis/orders/helpers/getProductId.feature')
    * def productId = product.productId
    * def wishlistPath = '/v1/my-wishlist'

  @GS3-66
  Scenario: Add product to wishlist → Verify it appears in wishlist → Clean up
    # Add to wishlist
    Given headers authHeader
    And path wishlistPath, productId
    When method put
    Then status 204

    # Verify it appears in wishlist
    Given headers authHeader
    And path wishlistPath
    And param lang = 'en'
    When method get
    Then status 200
    * match response.content[*].id contains productId

    # Remove from wishlist
    Given headers authHeader
    And path wishlistPath, productId
    When method delete
    Then status 204
