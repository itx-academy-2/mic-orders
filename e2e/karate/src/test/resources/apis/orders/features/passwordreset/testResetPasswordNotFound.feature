Feature: Reset password - token not found

  Background:
    * def vars = call read('classpath:apis/orders/features/passwordreset/testPasswordResetBackground.feature')
    * def retailBasePath = vars.retailBasePath
    * def resetPasswordRequest = vars.resetPasswordRequest
    * url vars.urls.retailApiUrl

  Scenario: Reset password - token not found
    Given path retailBasePath
    And request resetPasswordRequest
    When method PUT
    Then status 404
    And match response.title == 'Not Found'
    And match response.detail == vars.validToken



