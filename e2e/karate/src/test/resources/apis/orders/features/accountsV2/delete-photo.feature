Feature: Delete User Profile Photo

  Background:
    * url urls.retailApiUrl
    * def authHeader = callonce read('classpath:karate-auth.js')
    * def photoPath = '/v1/my-info/photo'
    * def originalState = callonce read('classpath:apis/orders/helpers/account/get-original-photo.feature') { authHeader: '#(authHeader)' }
    * def originalPhoto = originalState.response.photo

  @GS3-87 @write
  Scenario: Successfully delete a user photo and restore original state
  # 1. SETUP: Ensure there is a photo to delete
    * def photoToDelete = 'https://example.com/photo-to-delete-' + java.util.UUID.randomUUID() + '.jpg'
    Given headers authHeader
    And path photoPath
    And request { photo: '#(photoToDelete)' }
    When method put
    Then status 204

  # 2. ACT: Delete the photo
    Given headers authHeader
    And path photoPath
    When method delete
    Then status 204

  # 3. ASSERT: Verify the photo is now null
    Given headers authHeader
    And path photoPath
    When method get
    Then status 200
    And match response.photo == null

  # 4. CLEANUP: Restore the original state using our helper
    * call read('classpath:apis/orders/helpers/account/restore-photo.feature') { authHeader: '#(authHeader)', photoToRestore: '#(originalPhoto)' }

  @GS3-87
  Scenario: Attempt to delete photo without authorization
    Given path photoPath
    When method delete
    Then status 401