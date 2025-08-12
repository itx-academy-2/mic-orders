Feature: Get User Profile Photo

  Background:
    * url urls.retailApiUrl
    * def authHeader = callonce read('classpath:karate-auth.js')
    * def photoPath = '/v1/my-info/photo'

  @GS3-87
  Scenario: Successfully get user photo as an authorized user
    Given headers authHeader
    And path photoPath
    When method get
    Then status 200
    * if (response.photo != null) karate.match(response.photo, '#string')

  @GS3-87
  Scenario: Attempt to get user photo without authorization
    Given path photoPath
    When method get
    Then status 401