package com.academy.orders.infrastructure.wishlist.repository;

import com.academy.orders.domain.wishlist.exception.UnsupportedSortFieldException;
import com.academy.orders.infrastructure.product.entity.ProductTranslationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface WishlistProductTranslationJpaAdapter extends JpaRepository<ProductTranslationEntity, UUID> {

  /**
   * Finds paginated ProductTranslationEntities that belong to the wishlist of the given account, filtered by language and only visible
   * products.
   *
   * @param accountId the account ID whose wishlist is queried
   * @param language the language code (e.g. "en")
   * @param pageable paging and sorting information
   * @return {@link Page<ProductTranslationEntity>} a page of ProductTranslationEntity matching the criteria
   */
  @Query("""
          SELECT pt FROM ProductTranslationEntity pt
          JOIN  pt.product p
          JOIN  pt.language l
          LEFT JOIN  p.tags t
          JOIN WishlistEntity w ON w.id.productId = p.id
          WHERE w.id.accountId = :accountId
            AND l.code = :language
            AND p.status = 'VISIBLE'
      """)
  Page<ProductTranslationEntity> findWishlistProductTranslations(@Param("accountId") Long accountId, @Param("language") String language,
      Pageable pageable);

  /**
   * Remaps sorting fields from external API values to the actual entity field names used in the query. <p> This allows API clients to use
   * logical field names (e.g. "name", "addedAt", "product.price") while internally the query uses the correct entity-specific fields (e.g.
   * "pt.name", "w.addedAt", "p.price" where "pt", "w", "p" are aliases used in the SQL query). <p> If an unsupported sort field is
   * provided, an {@link UnsupportedSortFieldException} is thrown.
   *
   * @param original the original {@link Pageable} from the API request
   * @return a new {@link Pageable} instance with remapped sort orders
   * @throws UnsupportedSortFieldException if the sort field is not recognized
   */
  static Pageable remapSort(Pageable original) {
    Sort remappedSort = Sort.by(original.getSort().stream()
        .map(order -> {
          String property = order.getProperty();

          return switch (property) {
            case "product.price" -> new Sort.Order(order.getDirection(), "p.price");
            case "name" -> new Sort.Order(order.getDirection(), "pt.name");
            case "addedAt" -> new Sort.Order(order.getDirection(), "w.addedAt");
            default -> throw new UnsupportedSortFieldException(property);
          };
        }).toList());
    return PageRequest.of(original.getPageNumber(), original.getPageSize(), remappedSort);
  }
}
