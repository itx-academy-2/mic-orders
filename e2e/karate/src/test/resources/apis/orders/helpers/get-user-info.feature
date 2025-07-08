@ignore
Feature: Get current user personal info (helper)

  Background:
    * url urls.retailApiUrl
    * def authHeader = callonce read('classpath:karate-auth.js')

  Scenario: Get current user info
    Given headers authHeader
    And path '/v2/myInfo'
    When method get
    Then status 200
    * def originalUserData =
      """
      {
        "firstName": "#(response.firstName)",
        "lastName": "#(response.lastName)",
        "phone": #(response.phone == 'null' ? null : response.phone)
      }
      """