Feature: Get order by id

  Background:
    * url urls.retailApiUrl

  Scenario Outline: Get a non-existing order and an order by a user
    Given def testDataFile = call utils.readTestData <testDataFile>
    * def credentials = { username: <username>, password: <password> }
    * def authHeader = call read('classpath:karate-auth.js')(credentials)
    And path '/v1/management/orders', <orderId>
    And headers authHeader
    When method Get
    Then match responseStatus == <status>
    And match response == testDataFile
    Examples:
      | status | role      | username                  | password                  | orderId                                | testDataFile                                                      |
      | 404    | 'MANAGER' | '#(manager.username)'     | '#(manager.password)'     | '11111111-e29b-41d4-a716-446655440001' | 'classpath:apis/orders/test-data/responses/response_4xx.json'     |
      | 403    | 'USER'    | '#(user.username)'        | '#(user.password)'        | '550e8400-e29b-41d4-a716-446655440015' | 'classpath:apis/orders/test-data/responses/response_4xx.json'     |
  Scenario Outline: Get an order without a discount
    Given def credentials = { username: <username>, password: <password> }
    Given def authHeader = call read('classpath:karate-auth.js')(credentials)
    And path '/v1/management/orders/550e8400-e29b-41d4-a716-446655440015'
    And headers authHeader
    When method Get
    Then match responseStatus == 200
    * def expectedResponse = read(<testDataFile>)
    And match response == expectedResponse
    And match each response.orderItems ==
      """
      {
        "price": "#number",
        "priceWithDiscount": null,
        "discount": null,
        "quantity": "#number",
        "product": {
          "id": "#string",
          "name": "#string",
          "description": "#string",
          "status": "#string",
          "tags": "#array",
          "image": "#string",
          "price": "#number",
          "discount": "##number",
          "priceWithDiscount": "##number"
        }
      }
      """
    Examples:
      | role      | username                  | password                  | testDataFile                                                      |
      | 'MANAGER' | '#(manager.username)'     | '#(manager.password)'     | 'classpath:apis/orders/test-data/responses/getOrderById_200.json' |
      | 'ADMIN'   | '#(credentials.username)' | '#(credentials.password)' | 'classpath:apis/orders/test-data/responses/getOrderById_200.json' |
  Scenario Outline: Get an order with at least one discounted product
    Given def credentials = { username: '#(manager.username)', password: '#(manager.password)' }
    Given def authHeader = call read('classpath:karate-auth.js')(credentials)
    And path '/v1/management/orders/550e8400-e29b-41d4-a716-446655440016'
    And headers authHeader
    When method Get
    Then match responseStatus == 200
    * def expectedResponse = read(<testDataFile>)
    And match response == expectedResponse
    And match each response.orderItems ==
      """
      {
        "price": "#number",
        "priceWithDiscount": "##number",
        "discount": "##number",
        "quantity": "#number",
        "product": {
          "id": "#string",
          "name": "#string",
          "description": "#string",
          "status": "#string",
          "tags": "#array",
          "image": "#string",
          "price": "#number",
          "discount": "##number",
          "priceWithDiscount": "##number"
        }
      }
      """
    * def itemWithDiscountRelatedFields = response.orderItems.find(x => x.priceWithDiscount != null && x.discount != null)
    * assert itemWithDiscountRelatedFields != null
    Examples:
      | role      | username                  | password                  | testDataFile                                                                  |
      | 'MANAGER' | '#(manager.username)'     | '#(manager.password)'     | 'classpath:apis/orders/test-data/responses/getOrderWithDiscountById_200.json' |
      | 'ADMIN'   | '#(credentials.username)' | '#(credentials.password)' | 'classpath:apis/orders/test-data/responses/getOrderWithDiscountById_200.json' |