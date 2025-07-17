@ignore
Feature: Get current user personal info (helper)

  Background:
    * url urls.retailApiUrl
    * def authHeader = callonce read('classpath:karate-auth.js')
    * def myInfoPath = '/v2/my-info'

  @GS3-51
  Scenario: Get current user info
    Given headers authHeader
    And path myInfoPath
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