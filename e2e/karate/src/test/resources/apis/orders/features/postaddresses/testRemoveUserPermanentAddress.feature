Feature: Remove permanent user post address

  Background:
    * url urls.retailApiUrl
    * def userId = 2
    * def fileName = 'classpath:config-' + karate.env + '-secrets.yml'
    * def secrets = read(fileName)

  Scenario: Make a permanent post address temporary for the user
    * def credentials = { username: '#(secrets.user.username)', password: '#(secrets.user.password)', authMode: 'token' }
    * def authHeader = call read('classpath:karate-auth.js') credentials
    * call read('classpath:apis/orders/helpers/add-permanent-post-address.feature')
    * def DbUtils = Java.type('com.academy.orders.karate.db.DbUtils')
    * def db = new DbUtils(datasource)
    * def testAddressId = db.getPostAddressIdByTitle('permanent: Friend')
    * match testAddressId != null

    * def beforeTitle = db.getPostAddressTitleById(testAddressId)
    * match beforeTitle == '#regex ^permanent:\\s*.*'

    Given headers authHeader
    And path 'v1/users/' + userId + '/addresses/' + testAddressId
    When method DELETE
    Then status 204

    * def afterTitle = db.getPostAddressTitleById(testAddressId)
    * match afterTitle == '#regex ^temp:\\s*.*'

    * eval db.executeUpdateWithParams("DELETE FROM post_addresses_v2 WHERE id = ?", testAddressId)

  Scenario: Second attempt to change already temporary title is unsuccessful
    * def credentials = { username: '#(secrets.user.username)', password: '#(secrets.user.password)', authMode: 'token' }
    * def authHeader = call read('classpath:karate-auth.js') credentials
    * call read('classpath:apis/orders/helpers/add-permanent-post-address.feature')
    * def DbUtils = Java.type('com.academy.orders.karate.db.DbUtils')
    * def db = new DbUtils(datasource)
    * def testAddressId = db.getPostAddressIdByTitle('permanent: Friend')
    * match testAddressId != null

    * def beforeTitle = db.getPostAddressTitleById(testAddressId)
    * match beforeTitle == '#regex ^permanent:\\s*.*'

    Given headers authHeader
    And path 'v1/users/' + userId + '/addresses/' + testAddressId
    When method DELETE
    Then status 204

    * def afterTitle = db.getPostAddressTitleById(testAddressId)
    * match afterTitle == '#regex ^temp:\\s*.*'
    * match afterTitle != beforeTitle

    Given headers authHeader
    And path 'v1/users/' + userId + '/addresses/' + testAddressId
    When method DELETE
    Then status 204

    * def titleAfterSecondAttempt = db.getPostAddressTitleById(testAddressId)
    * match afterTitle == titleAfterSecondAttempt

    * eval db.executeUpdateWithParams("DELETE FROM post_addresses_v2 WHERE id = ?", testAddressId)

  Scenario: Get 403 Forbidden status while trying to remove the other user's post address
    * def credentials = { username: '#(secrets.user.username)', password: '#(secrets.user.password)', authMode: 'token' }
    * def authHeader = call read('classpath:karate-auth.js') credentials
    * def otherUserId = 5
    * def randomAddressId = java.util.UUID.randomUUID();

    Given headers authHeader
    And path 'v1/users/' + otherUserId + '/addresses/' + randomAddressId
    When method DELETE
    Then status 403

  Scenario: Get 401 Unauthorised status while trying to remove the post address without authorisation
    * def randomAddressId = java.util.UUID.randomUUID();

    And path 'v1/users/' + userId + '/addresses/' + randomAddressId
    When method DELETE
    Then status 401

  Scenario: Account not found exception
    * def credentials = { username: '#(secrets.credentials.username)', password: '#(secrets.credentials.password)', authMode: 'token' }
    * def authHeader = call read('classpath:karate-auth.js') credentials
    * def randomAddressId = java.util.UUID.randomUUID();
    * def nonexistentUserId = 88

    Given headers authHeader
    And path 'v1/users/' + nonexistentUserId + '/addresses/' + randomAddressId
    When method DELETE
    Then status 404
    * def expectedMsg = 'Account with id: ' + nonexistentUserId + ' is not found'
    And match response.detail == expectedMsg

  Scenario: Post address not found exception
    * def credentials = { username: '#(secrets.user.username)', password: '#(secrets.user.password)', authMode: 'token' }
    * def authHeader = call read('classpath:karate-auth.js') credentials
    * def randomAddressId = java.util.UUID.randomUUID();

    Given headers authHeader
    And path 'v1/users/' + userId + '/addresses/' + randomAddressId
    When method DELETE
    Then status 404
    * def expectedMsg = 'PostAddress with id: ' + randomAddressId + ' is not found'
    And match response.detail == expectedMsg