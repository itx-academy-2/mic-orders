Feature: Create OrderV2

  Background:
    * def DbUtils = Java.type('com.academy.orders.karate.db.DbUtils')
    * def db = new DbUtils(datasource)
    * def productFeature = callonce read('classpath:apis/orders/helpers/getProductId.feature')
    * def productId = productFeature.productId
    * def authHeader = callonce read('classpath:karate-auth.js')
    * def userId = credentials.id
    * def requestData = call utils.readTestData 'classpath:apis/orders/test-data/requests/createOrderV2Request.json'
    * url urls.retailApiUrl

  Scenario: Create orderV2 (Created 201)
    * def cartCleanup = db.executeUpdateWithParams("DELETE FROM cart_items WHERE user_id = ?", java.lang.Long.parseLong(userId))
    * def quantityBeforeCreation = db.getProductQuantity(productId)
    * eval db.increaseProductQuantity(productId)

    Given headers authHeader
    And path 'v1/users/' + userId + '/cart/' + productId
    When method POST
    Then status 201

    Given headers authHeader
    And path 'v2/users/' + userId + '/orders'
    And request requestData
    When method POST
    Then status 201
    And match response.orderId == "#string"

    * def orderId = response.orderId
    * def cartCleanup = db.executeUpdateWithParams("DELETE FROM cart_items WHERE user_id = ?", java.lang.Long.parseLong(userId))
    * def quantityAfterCreation = db.getProductQuantity(productId)

    And match quantityBeforeCreation == quantityAfterCreation

    Given headers authHeader
    And path 'v1/users/' + userId +'/cart/items'
    When method GET
    Then status 200
    And match response.items == '#[0]'

    * def userId = credentials.id
    * def orderId = java.util.UUID.fromString(orderId)
    * def postAddressV2Id = java.util.UUID.fromString(db.getPostAddressIdByOrderId(orderId))
    * def orderItemsDeleted2 = db.executeUpdateWithParams("DELETE FROM order_items_v2 WHERE order_v2_id = ?", orderId)
    * def ordersDeletedV2 = db.executeUpdateWithParams("DELETE FROM orders_v2 WHERE id = ?", orderId)
    * def postAddressDeletedV1 = db.executeUpdateWithParams("DELETE FROM post_addresses WHERE id = ?", orderId)
    * def orderItemsDeletedV1 = db.executeUpdateWithParams("DELETE FROM order_items WHERE order_id = ?", orderId)
    * def ordersDeletedV1 = db.executeUpdateWithParams("DELETE FROM orders WHERE id = ?", orderId)
    * def postAddressDeletedV2 = db.executeUpdateWithParams("DELETE FROM post_addresses_v2 WHERE id = ?", postAddressV2Id)
    * def cartCleanup = db.executeUpdateWithParams("DELETE FROM cart_items WHERE user_id = ?", java.lang.Long.parseLong(userId))

  Scenario: Create orderV2 (Bad Request 400 - empty cart)
    Given headers authHeader
    And path 'v2/users/' + userId + '/orders'
    And request requestData
    When method POST
    Then status 400

  Scenario Outline: Create orderV2 (Bad Request 400 - incorrect <message>)
    * set requestData.firstName = '<firstName>'
    * set requestData.lastName = '<lastName>'
    * set requestData.phone = '<phone>'
    * set requestData.city = '<city>'
    * set requestData.department = '<department>'
    * set requestData.title = '<title>'

    * def quantityBefore = db.getProductQuantity(productId)
    * eval db.increaseProductQuantity(productId)
    * def cartCleanup = db.executeUpdateWithParams("DELETE FROM cart_items WHERE user_id = ?", java.lang.Long.parseLong(userId))

    Given headers authHeader
    And path 'v1/users/' + userId + '/cart/' + productId
    When method POST
    Then status 201

    Given headers authHeader
    And path 'v2/users/' + userId + '/orders'
    And request requestData
    When method POST
    Then status 400

    Examples:
      | firstName | lastName | phone         | city    | department          | title          | message    |
      | A         | LastName | +380960998877 | Kharkiv | №1 Franka street, 7 |                | firstName  |
      | firstName | L        | +380960998877 | Kharkiv | №1 Franka street, 7 | Friend         | lastName   |
      | firstName | LastName | 3             | Kharkiv | №1 Franka street, 7 |                | phone      |
      | firstName | LastName | +380960998877 | K       | №1 Franka street, 7 |                | city       |
      | firstName | LastName | +380960998877 | Kharkiv | №1 Franka street, 7 | " "            | title      |

  Scenario: Create orderV2 (Conflict 409 - duplicate title)
    * def quantityBefore = db.getProductQuantity(productId)
    * eval db.increaseProductQuantity(productId)
    * def cartCleanup = db.executeUpdateWithParams("DELETE FROM cart_items WHERE user_id = ?", java.lang.Long.parseLong(userId))

    Given headers authHeader
    And path 'v1/users/' + userId + '/cart/' + productId
    When method POST
    Then status 201

    * set requestData.title = 'MyOrderTitle'

    Given headers authHeader
    And path 'v2/users/' + userId + '/orders'
    And request requestData
    When method POST
    Then status 201

    * def orderId = response.orderId
    * eval db.increaseProductQuantity(productId)

    Given headers authHeader
    And path 'v1/users/' + userId + '/cart/' + productId
    When method POST
    Then status 201

    * set requestData.firstName = 'Joe'

    Given headers authHeader
    And path 'v2/users/' + userId + '/orders'
    And request requestData
    When method POST
    Then status 409

    * def orderId = java.util.UUID.fromString(orderId)
    * def postAddressV2Id = java.util.UUID.fromString(db.getPostAddressIdByOrderId(orderId))
    * def orderItemsDeleted2 = db.executeUpdateWithParams("DELETE FROM order_items_v2 WHERE order_v2_id = ?", orderId)
    * def ordersDeletedV2 = db.executeUpdateWithParams("DELETE FROM orders_v2 WHERE id = ?", orderId)
    * def postAddressDeletedV1 = db.executeUpdateWithParams("DELETE FROM post_addresses WHERE id = ?", orderId)
    * def orderItemsDeletedV1 = db.executeUpdateWithParams("DELETE FROM order_items WHERE order_id = ?", orderId)
    * def ordersDeletedV1 = db.executeUpdateWithParams("DELETE FROM orders WHERE id = ?", orderId)
    * def postAddressDeletedV2 = db.executeUpdateWithParams("DELETE FROM post_addresses_v2 WHERE id = ?", postAddressV2Id)
    * def cartCleanup = db.executeUpdateWithParams("DELETE FROM cart_items WHERE user_id = ?", java.lang.Long.parseLong(userId))





