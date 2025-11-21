package com.academy.orders.domain.pexels.usecase;

import java.util.List;

/**
 * Use case for searching image candidates from the Pexels API.
 *
 * <p>This use case allows the application layer to request a list of image URLs based on a search keyword. The implementation is provided
 * by an external REST client adapter located in the orders-api-rest-clients module.</p>
 */
public interface PexelsImageSearchUseCase {

  /**
   * Searches Pexels for image candidates matching the given keyword.
   *
   * @param query the search phrase used to find relevant images (e.g., "smartphone").
   * @return a list of direct image URLs in their original resolution.
   */
  List<String> searchImages(String query);
}
