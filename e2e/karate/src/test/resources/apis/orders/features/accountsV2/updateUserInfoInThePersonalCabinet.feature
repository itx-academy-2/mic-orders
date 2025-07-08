Feature: Update personal user info (PATCH)

  Background:
    * url urls.retailApiUrl
    * def authHeader = callonce read('classpath:karate-auth.js')
    * def userInfo = call read('classpath:apis/orders/helpers/get-user-info.feature')
    * def originalUserData = userInfo.originalUserData

  @GS3-51
  Scenario: Update myInfo - authorized user
    # In this test:
    # 1. We first retrieve the original user data.
    # 2. Then we update the user info with new values.
    # 3. Next, we verify that the update was applied correctly by fetching the data again.
    # 4. Finally, we restore the original user data to leave the system in its initial state.

    # Update user info with new data
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

    # Verify that data was updated successfully
    Given headers authHeader
    And path '/v2/myInfo'
    When method get
    Then status 200
    * match response.firstName == "Updated"
    * match response.lastName == "User"
    * match response.phone == "+380631234567"

    # Restore original data
    Given headers authHeader
    And header Content-Type = 'application/json'
    And path '/v2/myInfo'
    And request originalUserData
    When method patch
    Then status 204

  @GS3-51
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

  @GS3-51
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