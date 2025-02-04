package com.academy.orders.infrastructure.product.repository;

import com.academy.orders.domain.product.dto.ProductManagementFilterDto;
import com.academy.orders.domain.product.entity.enumerated.ProductStatus;
import com.academy.orders.infrastructure.product.entity.ProductEntity;
import com.academy.orders.infrastructure.product.entity.ProductTranslationEntity;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductJpaAdapter extends JpaRepository<ProductEntity, UUID> {
	/**
	 * Finds a paginated list of products by language code and with a visible
	 * status, sorted by the specified criteria.
	 *
	 * @param language
	 *            the language code for filtering products.
	 * @param pageable
	 *            the pagination information.
	 *
	 * @return a {@link Page} containing the filtered list of {@link ProductEntity}
	 *         objects.
	 *
	 * @author Anton Bondar, Denys Liubchenko
	 */
	@Query(value = "SELECT pt FROM ProductTranslationEntity pt LEFT JOIN FETCH pt.product p "
			+ "LEFT JOIN pt.language l LEFT JOIN p.tags t WHERE l.code = :language AND p.status = 'VISIBLE' "
			+ "AND (:#{#tags.isEmpty()} = true OR t.name IN :tags)")
	Page<ProductTranslationEntity> findAllByLanguageCodeAndStatusVisible(String language, Pageable pageable,
			List<String> tags);

	/**
	 * Retrieves a paginated list of {@link ProductTranslationEntity} based on the
	 * specified language code, visible product status, and non-null discount.
	 *
	 * @param language
	 *            The language code to filter the product translations.
	 * @param pageable
	 *            The pagination information.
	 * @return A paginated {@link Page} of {@link ProductTranslationEntity}.
	 */
	@Query("""
			SELECT pt \
			FROM ProductTranslationEntity AS pt \
			INNER JOIN FETCH pt.product AS p \
			INNER JOIN FETCH pt.language AS l \
			INNER JOIN FETCH p.discount \
			WHERE l.code= :language AND p.status = 'VISIBLE'""")
	Page<ProductTranslationEntity> findAllByLanguageCodeAndStatusVisibleAndDiscountNotNull(String language,
			Pageable pageable);

	/**
	 * Finds a paginated list of products by language code and with a visible
	 * status, sorted by amount of order items with this product.
	 *
	 * @param language
	 *            the language code for filtering products.
	 * @param pageable
	 *            the pagination information.
	 *
	 * @return a {@link Page} containing the filtered list of {@link ProductEntity}
	 *         objects.
	 *
	 * @author Denys Liubchenko
	 */
	@Query("SELECT p FROM ProductEntity p LEFT JOIN p.orderItems oi LEFT JOIN p.productTranslations pt "
			+ "LEFT JOIN pt.language l LEFT JOIN p.tags t  WHERE l.code = :language AND p.status = 'VISIBLE' "
			+ "AND (:#{#tags.isEmpty()} = true OR t.name IN :tags) GROUP BY p.id "
			+ "ORDER BY count(oi.orderItemId.productId) desc")
	Page<ProductEntity> findAllByLanguageCodeAndStatusVisibleOrderedByDefault(String language, Pageable pageable,
			List<String> tags);

	/**
	 * Finds a list of products by their IDs and language code.
	 *
	 * @param productIds
	 *            the list of product IDs.
	 * @param language
	 *            the language code for filtering products.
	 * @return a {@link List} of {@link ProductEntity} objects.
	 * @author Denys Liubchenko
	 */
	@Query("SELECT p FROM ProductEntity p LEFT JOIN FETCH p.productTranslations pt "
			+ "LEFT JOIN FETCH pt.language l LEFT JOIN FETCH p.tags t "
			+ "WHERE p.id in (:productIds) and l.code = :language")
	List<ProductEntity> findAllByIdAndLanguageCode(List<UUID> productIds, String language);

	/**
	 * Updates the quantity of a product.
	 *
	 * @param id
	 *            the ID of the product.
	 * @param quantity
	 *            the new quantity to set.
	 * @author Denys Ryhal
	 */
	@Modifying
	@Query(nativeQuery = true, value = "UPDATE products SET quantity = :quantity WHERE id = :id")
	void setNewProductQuantity(UUID id, Integer quantity);

	/**
	 * Finds paginated product IDs by language code and filter criteria.
	 *
	 * @param lang
	 *            the language code for filtering products.
	 * @param filter
	 *            the filter criteria.
	 * @param pageable
	 *            the pagination information.
	 * @return a {@link Page} containing the filtered list of product IDs.
	 * @author Denys Ryhal
	 */
	@Query("SELECT p.id FROM ProductEntity p JOIN p.productTranslations pt JOIN pt.language l "
			+ "LEFT JOIN p.tags t WHERE l.code = :lang "
			+ "AND (:#{#filter.status} IS NULL OR p.status = :#{#filter.status})"
			+ "AND (:#{#filter.searchByName} IS NULL OR LOWER(pt.name) LIKE LOWER(CONCAT('%', :#{#filter.searchByName}, '%'))) "
			+ "AND (:#{#filter.quantityLess} IS NULL OR p.quantity <= :#{#filter.quantityLess}) "
			+ "AND (:#{#filter.quantityMore} IS NULL OR p.quantity >= :#{#filter.quantityMore}) "
			+ "AND (:#{#filter.priceLess} IS NULL OR p.price <= :#{#filter.priceLess}) "
			+ "AND (:#{#filter.priceMore} IS NULL OR p.price >= :#{#filter.priceMore}) "
			+ "AND (coalesce(:#{#filter.createdBefore}, NULL) IS NULL OR p.createdAt <= :#{#filter.createdBefore}) "
			+ "AND (coalesce(:#{#filter.createdAfter}, NULL) IS NULL OR p.createdAt >= :#{#filter.createdAfter})"
			+ "AND (:#{#filter.tags.isEmpty()} = true OR t.name IN :#{#filter.tags})")
	Page<UUID> findProductsIdsByLangAndFilters(String lang, @NonNull ProductManagementFilterDto filter,
			Pageable pageable);

	/**
	 * Finds a list of products by their IDs and language code, sorted by the
	 * specified criteria.
	 *
	 * @param lang
	 *            the language code for filtering products.
	 * @param ids
	 *            the list of product IDs.
	 * @param sort
	 *            the sort criteria.
	 * @return a {@link List} of {@link ProductEntity} objects.
	 * @author Denys Ryhal
	 */
	@Query("SELECT p FROM ProductEntity p JOIN FETCH p.productTranslations pt "
			+ "JOIN FETCH pt.language l LEFT JOIN FETCH p.tags t WHERE (p.id IN :ids) AND pt.language.code = :lang")
	List<ProductEntity> findProductsByIds(String lang, List<UUID> ids, Sort sort);

	/**
	 * Updates the status of a product.
	 *
	 * @param id
	 *            the ID of the product.
	 * @param status
	 *            the new status to set.
	 * @author Denys Liubchenko
	 */
	@Modifying
	@Query("UPDATE ProductEntity SET status = :status WHERE id = :id")
	void updateProductStatus(UUID id, ProductStatus status);

	/**
	 * Finds product translations by product ID.
	 *
	 * @param id
	 *            the ID of the product.
	 * @return a {@link List} of {@link ProductTranslationEntity} objects.
	 * @author Anton Bondar
	 */
	@Query("SELECT pt FROM ProductTranslationEntity pt LEFT JOIN FETCH pt.product p "
			+ "LEFT JOIN FETCH pt.language l LEFT JOIN FETCH p.tags t WHERE p.id = :id")
	Set<ProductTranslationEntity> findTranslationsByProductId(UUID id);

	/**
	 * Retrieves a paginated list of ProductTranslationEntity objects based on a
	 * search query and language code.
	 *
	 * @param searchQuery
	 *            the search query to filter product names
	 * @param lang
	 *            the language code to filter product translations
	 * @param pageable
	 *            the pagination information
	 * @return a paginated list of ProductTranslationEntity objects that match the
	 *         search criteria
	 */
	@Query("SELECT pt FROM ProductTranslationEntity pt LEFT JOIN FETCH pt.product p LEFT JOIN pt.language l "
			+ "WHERE l.code = :lang AND LOWER(pt.name) LIKE LOWER(CONCAT('%', :searchQuery, '%'))")
	PageImpl<ProductTranslationEntity> findProductsByNameWithSearchQuery(String searchQuery, String lang,
			PageRequest pageable);

	/**
	 * Finds a {@link ProductEntity} by its ID. This method retrieves a product
	 * entity along with its associated product translations, languages, and tags
	 * based on the provided product ID.
	 *
	 * @param id
	 *            the ID of the product to find.
	 * @return an {@link Optional} containing the {@link ProductEntity} if found, or
	 *         an empty {@link Optional} if no product with the given ID exists.
	 * @author Anton Bondar
	 */
	@Query("SELECT p FROM ProductEntity p LEFT JOIN FETCH p.productTranslations pt "
			+ "LEFT JOIN FETCH pt.language LEFT JOIN FETCH p.tags WHERE p.id = :id")
	Optional<ProductEntity> findProductByProductId(UUID id);

	/**
	 * Finds a {@link ProductEntity} by its ID and language code. This method
	 * retrieves a product entity along with its associated product translations,
	 * languages, and tags based on the provided product ID and language code, and
	 * filters by products with status VISIBLE.
	 *
	 * @param id
	 *            the ID of the product to find.
	 * @param lang
	 *            the language code to filter the product translation.
	 * @return an {@link Optional} containing the {@link ProductEntity} if found, or
	 *         an empty {@link Optional} if no product with the given ID and
	 *         language code exists.
	 * @author Anton Bondar
	 */
	@Query("SELECT p FROM ProductEntity p LEFT JOIN FETCH p.productTranslations pt "
			+ "LEFT JOIN FETCH pt.language l LEFT JOIN FETCH p.tags "
			+ "WHERE p.id = :id AND l.code = :lang AND p.status = 'VISIBLE'")
	Optional<ProductEntity> findProductByProductIdAndLanguageCode(UUID id, String lang);
}
