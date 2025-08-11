Feature: Send password reset email - invalid email format

  Background:
    * header Accept-Language = 'en-US'
    * def vars = call read('classpath:apis/orders/features/passwordreset/testPasswordResetBackground.feature')
    * def retailBasePath = vars.retailBasePath
    * def resetEmailBadRequest = vars.resetEmailBadRequest
    * url vars.urls.retailApiUrl

  Scenario: Send password reset email - invalid email format
    Given path retailBasePath
    And request resetEmailBadRequest
    When method POST
    Then status 400
    And match response contains { title: 'Bad Request', detail: '#regex (?i).*email address.*' }

