package com.academy.orders.infrastructure.wishlist.repository;

import com.academy.orders.infrastructure.product.entity.ProductTranslationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
}
