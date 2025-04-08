Feature: Search articles
  Background:
    * url urls.retailApiUrl
  Scenario:
    Given path '/v1/articles/search'
    And param query = "Paypal"
    And param language = "en"
    When method Get
    Then status 200
    And match each response ==
    """
    {
      id: '#number',
      title: '#string'
    }
    """