Feature: Get current authenticated user info

  Background:
    * url urls.retailApiUrl
    * def authHeader = callonce read('classpath:karate-auth.js')

  Scenario: Get myInfo - authorized user
    Given headers authHeader
    And path '/v2/myInfo'
    When method get
    Then status 200
    And match response ==
  """
    {
      "email": "#string",
      "firstName": "#string",
      "lastName": "#string",
      "createdAt": "#string",
      "photo": "#? _ == null || typeof _ === 'string'",
      "phone": "#? _ == null || typeof _ === 'string'"
    }
    """

  Scenario: Get myInfo - unauthorized user
    Given path '/v2/myInfo'
    When method get
    Then status 401
