Feature: Send password reset email - success

  Background:
    * def vars = call read('classpath:apis/orders/features/passwordreset/testPasswordResetBackground.feature')
    * def retailBasePath = vars.retailBasePath
    * def resetEmailRequest = vars.resetEmailRequest
    * url vars.urls.retailApiUrl

  Scenario: Send password reset email - success
    Given path retailBasePath
    And request resetEmailRequest
    When method POST
    Then status 200
    And match response.message == "If the email exists, a password reset link has been sent"
    And match response.token == null
    And match response.timestamp != null
