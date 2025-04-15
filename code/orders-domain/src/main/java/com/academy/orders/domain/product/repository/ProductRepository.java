package com.academy.orders.domain.product.repository;

import com.academy.orders.domain.common.Page;
import com.academy.orders.domain.common.Pageable;
import com.academy.orders.domain.product.dto.DiscountAndPriceWithDiscountRangeDto;
import com.academy.orders.domain.product.dto.ProductBestsellersDto;
import com.academy.orders.domain.product.dto.ProductLanguageDto;
import com.academy.orders.domain.product.dto.ProductManagementFilterDto;
import com.academy.orders.domain.product.dto.ProductsOnSaleFilterDto;
import com.academy.orders.domain.product.entity.Product;
import com.academy.orders.domain.product.entity.ProductManagement;
import com.academy.orders.domain.product.entity.ProductTranslationManagement;
import com.academy.orders.domain.product.entity.enumerated.ProductStatus;
import org.springframework.lang.NonNull;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Repository interface for managing and loading products.
 */
public interface ProductRepository {
  /**
   * Retrieves a paginated list of products based on the provided language and pageable information.
   *
   * @param language the language code to filter the products.
   * @param pageable the pageable information for pagination and sorting.
   * @param tags list of tag's names for filtering.
   * @return a page containing the products that match the criteria.
   * @author Anton Bondar, Yurii Osovskyi, Denys Liubchenko
   */
  Page<Product> findAllProducts(String language, Pageable pageable, List<String> tags);

  /**
   * Retrieves a paginated list of products based on the provided language, tags, and bestsellers IDs.
   *
   * <p>This method filters products by language, tags, and may prioritize bestsellers, if the sorting option is given. It returns the
   * result in a paginated format.</p>
   *
   * @param language The language code to filter products by.
   * @param pageable The pagination information (page number, size, sorting).
   * @param tags A list of tags to filter products by. If empty, no filtering is applied.
   * @param bestsellersIds A list of product IDs
   *
   * @return A paginated list of products matching the specified criteria.
   */
  Page<Product> findAllProducts(String language, Pageable pageable, List<String> tags, List<UUID> bestsellersIds);

  /**
   * Retrieves a paginated list of products based on the provided language and pageable information sorted by default. Sorting in pageable
   * will be ignored.
   *
   * @param language the language code to filter the products.
   * @param pageable the pageable information for pagination.
   * @param tags list of tag's names for filtering.
   * @return a page containing the products that match the criteria.
   * @author Denys Liubchenko
   */
  Page<Product> findAllProductsWithDefaultSorting(String language, Pageable pageable, List<String> tags);

  /**
   * Method sets new quantity of products.
   *
   * @param productId id of the product.
   * @param quantity new quantity of the product.
   * @author Denys Ryhal
   */
  void setNewProductQuantity(UUID productId, Integer quantity);

  /**
   * Method checks if product with id already exists.
   *
   * @param id id of the product.
   * @return {@link Boolean} true if product exists and false otherwise.
   * @author Denys Ryhal
   */
  boolean existById(UUID id);

  /**
   * Updates the status of a product.
   *
   * @param productId The unique identifier of the product whose status will be updated.
   * @param status The new status for the product, represented by {@link ProductStatus}.
   * @author Denys Liubchenko
   */
  void updateStatus(UUID productId, ProductStatus status);

  /**
   * Retrieves a list of {@link ProductTranslationManagement} entities by the unique identifier of the product.
   *
   * @param productId the unique identifier of the product.
   * @return a set of {@link ProductTranslationManagement} entities.
   * @author Anton Bondar
   */
  Set<ProductTranslationManagement> findTranslationsByProductId(UUID productId);

  /**
   * Saves a new product using the provided product creation request data transfer object (DTO). Returns the saved product entity.
   *
   * @param product the DTO containing the information necessary to create a new product.
   * @return the saved product entity.
   * @author Yurii Osovskyi
   */
  Product save(ProductManagement product);

  /**
   * Update an {@link ProductManagement} entity.
   *
   * @param product the {@link ProductManagement} entity to be updated.
   * @author Anton Bondar
   */
  void update(ProductManagement product);

  /**
   * Retrieves a paginated list of products filtered by language and additional criteria.
   *
   * @param language the language filter to apply to the products.
   * @param filter the additional criteria to filter the products.
   * @param pageable the pagination information.
   * @return a {@link Page} containing the filtered list of {@link Product} objects.
   * @author Denys Ryhal
   */
  Page<Product> findAllByLanguageWithFilter(String language, @NonNull ProductManagementFilterDto filter,
      Pageable pageable);

  /**
   * Retrieves a {@link Product} entity by its unique identifier.
   *
   * @param productId the unique identifier of the product.
   * @return the {@link Product} entity.
   * @author Anton Bondar
   */
  Optional<Product> getById(UUID productId);

  /**
   * Searches for products by their name based on the given search query and language.
   *
   * @param searchQuery the search term used to find products by name.
   * @param lang the language code for localizing the product names.
   * @param pageable the pagination information, specifying the page number, size, and sorting criteria.
   * @return a {@code Page} of {@code Product} objects that match the search criteria.
   * @author Denys Liubchenko
   */
  Page<Product> searchProductsByName(String searchQuery, String lang, Pageable pageable);

  /**
   * Retrieves an optional product based on its ID and language code.
   *
   * @param productId the unique identifier of the product.
   * @param lang the language code to filter the product translation.
   * @return an {@link Optional} containing the product if found, otherwise an empty {@link Optional}.
   */
  Optional<Product> getByIdAndLanguageCode(UUID productId, String lang);

  /**
   * Retrieves a paginated list of {@link Product} entities where the discount is available for the provided language. This method uses the
   * language to filter the products and only returns those that have a non-null discount.
   *
   * @param language the language code to filter the products (e.g., "en", "uk").
   * @param pageable the pagination information that includes the page number, size, and sorting.
   * @param bestsellersIds if the necessary filter option is given, then it will be sorted by this.
   * @return a {@link Page} of {@link Product} entities with a non-null discount, filtered by the given language and according to the
   *         provided pagination.
   */
  Page<Product> findProductsWhereDiscountIsNotNull(ProductsOnSaleFilterDto filter, String language, Pageable pageable,
      List<UUID> bestsellersIds);

  /**
   * Counts the number of products that have a discount applied. This method queries the database for all products where the discount field
   * is not null or greater than zero.
   *
   * @return the count of products with a discount.
   */
  int countByDiscountIsNotNull();

  /**
   * Retrieves the minimum and maximum price after discount, along with the minimum and maximum discount percentages applied to the
   * products. This method calculates and returns the following: 1. The lowest price of any product after the discount has been applied. 2.
   * The highest price of any product after the discount has been applied. 3. The lowest discount percentage applied to any product. 4. The
   * highest discount percentage applied to any product.
   */
  DiscountAndPriceWithDiscountRangeDto findDiscountAndPriceWithDiscountRange();

  /**
   * Retrieves a list of the most sold products within the specified date range and quantity.
   */
  List<ProductBestsellersDto> getIdsOfMostSoldProducts(LocalDateTime fromDate, LocalDateTime endDate, int quantity);

  /**
   * Retrieves a list of ProductLanguageDto for the given product IDs, prioritizing the provided language. If the language is not available,
   * then another will be chosen.
   *
   * @param lang The preferred language code
   * @param ids The list of product IDs to retrieve language-specific data for.
   * @return A list of ProductLanguageDto objects, each containing language-specific product details.
   */
  List<ProductLanguageDto> getProductLanguagesDto(String lang, List<UUID> ids);

  /**
   * Finds products by their IDs and language. If the requested language is not found, the method will attempt to find products using the
   * provided list of ProductLanguageDto.
   *
   * @param pageableDomain The pagination information.
   * @param language The language code to search for (e.g., "en", "es").
   * @param ids A list of product IDs to search for.
   * @param productLanguageDtos A list of ProductLanguageDto objects that contain product language data.
   * @return A paginated list of products matching the criteria.
   */
  Page<Product> findProductsByLanguageAndIds(Pageable pageableDomain, String language, List<UUID> ids,
      List<ProductLanguageDto> productLanguageDtos);

  Page<Product> findMostSoldProductsByTagAndPeriod(Pageable pageable, String language, LocalDateTime startDate, LocalDateTime endDate,
      String tag);
}
