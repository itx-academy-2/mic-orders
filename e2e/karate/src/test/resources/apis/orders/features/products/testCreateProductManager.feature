Feature: Create Product

  Background:
    * url urls.retailApiUrl
    # Automatically cleanup created products after each scenario
    * configure afterScenario =
    """
    function() {
        var status = karate.get('responseStatus');
        if (status == 201) {
            var id = karate.get('response.id');
            if (id) {
                karate.call('classpath:apis/orders/helpers/product/delete-product.feature', { id: id });
            }
        }
    }
    """

  @GS3-11
  Scenario Outline: Create Product
    * def credentials = { username: <username>, password: <password> }
    * def authHeader = call read('classpath:karate-auth.js')
    And path '/v1/management/products'
    And headers authHeader
    And request read('classpath:apis/orders/test-data/requests/createProductRequest.json')

    When method POST
    Then match responseStatus == <response>
    And match response == read(<testDataFile>)

    Examples:
      | response   | username                  | password                  | role      |testDataFile                                                         |
      | 201        | '#(manager.username)'     | '#(manager.password)'     | 'MANAGER' |'classpath:apis/orders/test-data/responses/createProduct_201.json'   |
      | 403        | '#(user.username)'        | '#(user.password)'        | 'USER'    |'classpath:apis/orders/test-data/responses/response_4xx.json'        |

