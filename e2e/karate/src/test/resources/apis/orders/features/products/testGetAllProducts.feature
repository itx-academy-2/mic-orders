Feature: Get list of all products

  Background:
    * url urls.retailApiUrl
    * def pathBase = '/v1/products'
    * def baseParams = { lang: 'en', page: 0, size: 8 }

  Scenario: Get all products sorted by price descending (considering discount)
    Given path pathBase
    And params baseParams
    And param sort = ['product.price', 'desc']
    When method GET
    Then status 200
    * def content = response.content
    * if (content.length == 0) karate.fail('No products returned')

    # Calculate effective price (after discount if present)
    * def prices =
      """
      content.map(x => {
        var discount = x.discount ? x.discount : 0;
        return x.price - (x.price * discount / 100);
      })
      """

    * def isSortedDesc =
      """
      function(prices) {
        for (var i = 0; i < prices.length - 1; i++) {
          if (prices[i] < prices[i + 1]) {
            karate.log('Out of order at index', i, ':', prices[i], '<', prices[i + 1]);
            return false;
          }
        }
        return true;
      }
      """
    * assert isSortedDesc(prices)

  Scenario: Get all products sorted by price ascending (considering discount)
    Given path pathBase
    And params baseParams
    And param sort = ['product.price', 'asc']
    When method GET
    Then status 200
    * def content = response.content
    * if (content.length == 0) karate.fail('No products returned')

    # Calculate effective price (after discount if present)
    * def prices =
      """
      content.map(x => {
        var discount = x.discount ? x.discount : 0;
        return x.price - (x.price * discount / 100);
      })
      """

    * def isSortedAsc =
      """
      function(prices) {
        for (var i = 0; i < prices.length - 1; i++) {
          if (prices[i] > prices[i + 1]) {
            karate.log('Out of order at index', i, ':', prices[i], '>', prices[i + 1]);
            return false;
          }
        }
        return true;
      }
      """
    * assert isSortedAsc(prices)

  Scenario: Get all products sorted by name ascending (case-insensitive)
    Given path pathBase
    And params baseParams
    And param sort = ['name', 'asc']
    When method GET
    Then status 200
    * def content = response.content
    * if (content.length == 0) karate.fail('No products returned')
    * def names = content.map(x => x.name)
    * def isSortedAsc =
      """
      function(names) {
        for (var i = 0; i < names.length - 1; i++) {
          if (names[i].toLowerCase() > names[i + 1].toLowerCase()) {
            karate.log('Out of order at index', i, ':', names[i], '>', names[i + 1]);
            return false;
          }
        }
        return true;
      }
      """
    * assert isSortedAsc(names)

  Scenario: Get all products with multiple filters applied
    Given path pathBase
    And params baseParams
    And param tags = 'category:computer,category:tablet,category:mobile'
    And param discount = true
    And param nonDiscount = true
    And param priceMin = 0
    And param priceMax = 10000
    And param availability = true
    And param nonAvailability = true
    And param deliveryNovaPost = true
    And param deliveryUkrPost = true
    When method GET
    Then status 200
    * def total = response.totalElements
    * if (total == 0) karate.fail('Expected products for filter, but got 0')
    * def minPrice = response.minProductPrice
    * def maxPrice = response.maxProductPrice
    * assert minPrice >= 0
    * assert maxPrice <= 10000

  @GS2-239
  Scenario: Validate structure of returned products
    Given path pathBase
    And params baseParams
    When method GET
    Then status 200
    And match response == read('classpath:apis/orders/test-data/responses/product/productSearchPageSchema.json')
    And match each response.content == read('classpath:apis/orders/test-data/responses/product/productPreviewSchema.json')