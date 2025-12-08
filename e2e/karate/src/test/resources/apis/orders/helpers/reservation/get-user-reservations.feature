@ignore
Feature: Get user reservations

  Background:
    * url urls.retailApiUrl
    * def reservationsPath = '/v1/my-reservations'
    * def authResult = call read('classpath:karate-auth.feature@fetchAuthToken') user
    * def token = authResult.response.token
    * def authHeader = { Authorization: '#("Bearer " + token)' }

  Scenario:
    Given headers authHeader
    And path reservationsPath
    And param lang = 'en'
    When method get
    Then status 200

    * def reservations = response

