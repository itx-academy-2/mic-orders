Feature: Search images using Pexels API through Orders API

  Background:
    * url urls.retailApiUrl
    * def credentials = manager
    * def authHeader = callonce read('classpath:karate-auth.js') credentials
    * def imagesPath = '/v1/management/products/images/search'

  @GS2-228
  Scenario: Search images → Verify response structure → Verify URLs returned
    Given headers authHeader
    And path imagesPath
    And param query = 'iphone'
    When method get
    Then status 200
    * match each response == '#regex ^https?://.*'