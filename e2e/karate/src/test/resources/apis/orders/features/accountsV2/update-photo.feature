Feature: Update User Profile Photo

  Background:
    * url urls.retailApiUrl
    * def authHeader = callonce read('classpath:karate-auth.js')
    * def photoPath = '/v1/my-info/photo'
    * def originalState = callonce read('classpath:apis/orders/helpers/account/get-original-photo.feature') { authHeader: '#(authHeader)' }
    * def originalPhoto = originalState.response.photo

  @GS3-87
  Scenario: Successfully update user photo and restore original state
    * def newPhoto = 'https://example.com/new-test-photo-' + java.util.UUID.randomUUID() + '.jpg'

  # 1. ACT: Update the photo
    Given headers authHeader
    And path photoPath
    And request { photo: '#(newPhoto)' }
    When method put
    Then status 204

  # 2. ASSERT: Verify the update was successful
    Given headers authHeader
    And path photoPath
    When method get
    Then status 200
    And match response.photo == newPhoto

  # 3. CLEANUP: Restore the original state using our helper
    * call read('classpath:apis/orders/helpers/account/restore-photo.feature') { authHeader: '#(authHeader)', photoToRestore: '#(originalPhoto)' }

  @GS3-87
  Scenario: Attempt to update photo with an invalid payload
    Given headers authHeader
    And path photoPath
    And request { photo: 'this-is-not-a-valid-url' }
    When method put
    Then status 400

  @GS3-87
  Scenario: Attempt to update photo without authorization
    Given path photoPath
    And request { photo: 'https://example.com/unauthorized.jpg' }
    When method put
    Then status 401