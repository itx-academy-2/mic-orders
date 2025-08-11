@ignore
Feature: Helper to restore the user photo to its original state

  Background:
    * url urls.retailApiUrl
    * headers authHeader
    * def photoPath = '/v1/my-info/photo'

  Scenario: Conditionally restore the original photo state
  # Step 1: Use JavaScript to DEFINE the request method and body
    * eval
  """
    if (photoToRestore) {
        karate.log('Preparing to restore with PUT:', photoToRestore);
        karate.set('requestMethod', 'put');
        karate.set('requestBody', { photo: photoToRestore });
    } else {
        karate.log('Preparing to restore null state with DELETE');
        karate.set('requestMethod', 'delete');
        karate.set('requestBody', null);
    }
    """

  # Step 2: USE the variables to build and send the request
    Given path photoPath
    And request requestBody
    When method requestMethod
    Then status 204