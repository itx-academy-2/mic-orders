Feature: Get count of discounted products
  Background:
    * url urls.retailApiUrl

  Scenario Outline:
    * def credentials = { username: <username>, password: <password> }
    * def authHeader = call read('classpath:karate-auth.js')
    When path '/v1/management/products/discounted/count'
    And headers authHeader
    And method Get

    Then match responseStatus == <response>
    Examples:
      | response   | username                  | password                  | role      |
      | 200        | '#(manager.username)'     | '#(manager.password)'     | 'MANAGER' |
      | 403        | '#(user.username)'        | '#(user.password)'        | 'USER'    |