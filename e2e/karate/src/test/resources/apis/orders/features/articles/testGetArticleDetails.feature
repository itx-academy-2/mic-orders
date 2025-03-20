Feature: Find an article by its id
  Background:
    * url urls.retailApiUrl
  Scenario: The article is found
    * def articleId = 1
    Given path '/v1/articles/details'
    When method Get
    Then status 200
    And match response ==
    """
    {
      "totalElements": "#number",
      "totalPages": "#number",
      "first": "#boolean",
      "last": "#boolean",
      "number": "#number",
      "numberOfElements": "#number",
      "size": "#number",
      "empty": "#boolean",
      "content": "##array"
    }
    """
    And match each response.content ==
    """
    {
      id: "#number",
      title: "#string"
    }
    """

