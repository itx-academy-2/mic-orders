@ignore
Feature: Helper to get the current user photo

  Background:
    * url urls.retailApiUrl
    * headers authHeader
    * def photoPath = '/v1/my-info/photo'

  Scenario: Fetch the current photo
    Given path photoPath
    When method get