Feature: Get all products for manager

  Background:
    * url urls.retailApiUrl
    * eval karate.set('credentials.username', manager.username)
    * eval karate.set('credentials.password', manager.password)
    * def authHeader = callonce read('classpath:karate-auth.js')
    * def pageSchema = read('classpath:apis/orders/test-data/responses/common/pageSchema.json')
    * def productSchema = read('classpath:apis/orders/test-data/responses/product/productResponseDTOSchema.json')

  @GS2-248
  Scenario: getAllManagerProducts
    Given headers authHeader
    And path '/v1/management/products'
    And params { size: 10, page: 0, priceMore: 500, priceLess: 2000 }
    When method GET
    Then status 200
    And match response == pageSchema
    And match each response.content == productSchema