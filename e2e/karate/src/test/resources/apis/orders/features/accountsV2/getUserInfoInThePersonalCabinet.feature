Feature: Get current authenticated user info

  Background:
    * url urls.retailApiUrl
    * def authHeader = callonce read('classpath:karate-auth.js')
    * def myInfoPath = '/v2/my-info'

  Scenario: Get myInfo - authorized user
    Given headers authHeader
    And path myInfoPath
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
    Given path myInfoPath
    When method get
    Then status 401
