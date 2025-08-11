Feature: Reset password - invalid token

  Background:
    * def vars = call read('classpath:apis/orders/features/passwordreset/testPasswordResetBackground.feature')
    * def retailBasePath = vars.retailBasePath
    * def resetPasswordInvalidTokenRequest = vars.resetPasswordInvalidTokenRequest
    * url vars.urls.retailApiUrl

  Scenario: Reset password - invalid token
    Given path retailBasePath
    And request resetPasswordInvalidTokenRequest
    When method PUT
    Then status 400
    And match response contains { title: 'Bad Request', detail: '#regex (?i).*invalid token.*' }
