Feature: Find an article by its id
  Background:
    * url urls.retailApiUrl
  Scenario: The article is found
    * def articleId = 1
    Given path '/v1/articles/' + articleId
    When method Get
    Then status 200
    And match response ==
    """
      {
        id: '#number',
        title: '#string',
        content: '#string',
        createdAt: '#string',
        updatedAt: '##string'
      }
    """
  Scenario: The article is not found
    * def articleId = 0
    Given path '/v1/articles/' + articleId
    When method Get
    Then status 404
    And match response ==
      """
      {
        status: '#number',
        title: '#string',
        detail: '##string'
      }
      """

