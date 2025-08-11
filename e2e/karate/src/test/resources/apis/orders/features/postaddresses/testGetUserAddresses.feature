Feature: Get permanent user post addresses

  Background:
    * url urls.retailApiUrl
    * def userId = 2
    * def fileName = 'classpath:config-' + karate.env + '-secrets.yml'
    * def secrets = read(fileName)

  Scenario: Get 2 permanent post addresses for the user of the three existing ones
  (the third one is temporary)
    * def credentials = { username: '#(secrets.user.username)', password: '#(secrets.user.password)', authMode: 'token' }
    * def authHeader = call read('classpath:karate-auth.js') credentials
    * call read('classpath:apis/orders/helpers/prepare-post-addresses.feature')

    Given headers authHeader
    And path 'v1/users/' + userId + '/addresses'
    When method GET
    Then status 200
    And match response == '#[2]'
    And match response[*].title contains only [ 'Friend', 'Family' ]

    * call read('classpath:apis/orders/helpers/delete-post-addresses.feature')

  Scenario: Get 403 Forbidden status while trying to get the other user's addresses
    * def credentials = { username: '#(secrets.user.username)', password: '#(secrets.user.password)', authMode: 'token' }
    * def authHeader = call read('classpath:karate-auth.js') credentials
    * def otherUserId = 5

    Given headers authHeader
    And path 'v1/users/' + otherUserId + '/addresses'
    When method GET
    Then status 403

  Scenario: Get 401 Unauthorised status while trying to get the addresses without authorisation
    And path 'v1/users/' + userId + '/addresses'
    When method GET
    Then status 401

  Scenario: Get 0 permanent post addresses for the user because he doesn't have them
    * def credentials = { username: '#(secrets.user.username)', password: '#(secrets.user.password)', authMode: 'token' }
    * def authHeader = call read('classpath:karate-auth.js') credentials

    Given headers authHeader
    And path 'v1/users/' + userId + '/addresses'
    When method GET
    Then status 200
    And match response == '#[0]'