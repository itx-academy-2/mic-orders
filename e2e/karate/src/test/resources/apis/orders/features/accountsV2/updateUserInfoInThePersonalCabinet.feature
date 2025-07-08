Feature: Update personal user info (PATCH)

  Background:
    * url urls.retailApiUrl
    * def authHeader = callonce read('classpath:karate-auth.js')
    * def userInfo = call read('classpath:apis/orders/helpers/get-user-info.feature')
    * def originalUserData = userInfo.originalUserData

  Scenario: Update myInfo - authorized user
    Given headers authHeader
    And header Content-Type = 'application/json'
    And path '/v2/myInfo'
    And request
    """
    {
      "firstName": "Updated",
      "lastName": "User",
      "phone": "+380631234567"
    }
    """
    When method patch
    Then status 204

    # Restore original data
    Given headers authHeader
    And header Content-Type = 'application/json'
    And path '/v2/myInfo'
    And request originalUserData
    When method patch
    Then status 204

  Scenario: Update myInfo - unauthorized user
    Given header Content-Type = 'application/json'
    And path '/v2/myInfo'
    And request
    """
    {
      "firstName": "Updated",
      "lastName": "User",
      "phone": "+380631234567"
    }
    """
    When method patch
    Then status 401

  Scenario: Update myInfo - invalid request
    Given headers authHeader
    And header Content-Type = 'application/json'
    And path '/v2/myInfo'
    And request
    """
    {
      "firstName": "",
      "lastName": "",
      "phone": "invalid"
    }
    """
    When method patch
    Then status 400
