@ignore
Feature: Remove product from user reservations

  Background:
    * url urls.retailApiUrl
    * def reservationsPath = '/v1/my-reservations'
    * def authResult = call read('classpath:karate-auth.feature@fetchAuthToken') user
    * def token = authResult.response.token
    * def authHeader = { Authorization: '#("Bearer " + token)' }

  Scenario:
    * def productId = __arg.productId
    Given headers authHeader
    And path reservationsPath, productId
    When method delete
    Then status 204

    * def result = { success: true }
