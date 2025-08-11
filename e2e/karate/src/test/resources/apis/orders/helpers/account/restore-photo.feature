@ignore
Feature: Helper to restore the user photo to its original state

  Background:
    * url urls.retailApiUrl
    * headers authHeader
    * def photoPath = '/v1/my-info/photo'
  # The 'photoToRestore' variable is passed in by the caller.

  Scenario: Conditionally restore the original photo state
  # Step 1: Use JavaScript to DEFINE the request method and body
    * eval
  """
    if (photoToRestore) {
        karate.log('Preparing to restore with PUT:', photoToRestore);
        // Set a variable for the request method
        karate.set('requestMethod', 'put');
        // Set a variable for the request body
        karate.set('requestBody', { photo: photoToRestore });
    } else {
        karate.log('Preparing to restore null state with DELETE');
        // Set a variable for the request method
        karate.set('requestMethod', 'delete');
        // No request body is needed for DELETE
    }
    """

  # Step 2: USE the variables to build and send the request
    Given path photoPath
  # 'request' will use the requestBody variable if it exists.
  # If requestBody was not set (in the DELETE case), this step is gracefully ignored.
    And request requestBody
  # 'method' uses the requestMethod variable ('put' or 'delete')
    When method requestMethod
  # The response for both successful PUT and DELETE is 204
    Then status 204