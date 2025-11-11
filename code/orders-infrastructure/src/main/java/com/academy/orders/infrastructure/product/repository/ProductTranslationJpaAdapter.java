package com.academy.orders.infrastructure.product.repository;

import com.academy.orders.infrastructure.product.entity.ProductTranslationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductTranslationJpaAdapter
    extends JpaRepository<ProductTranslationEntity, Long>, JpaSpecificationExecutor<ProductTranslationEntity> {

  /**
   * Returns ProductTranslationEntities for products present in user's reservation list. Only returns visible products.
   *
   * @param userId the ID of the user
   * @param language language code, e.g. "en"
   * @return list of ProductTranslationEntity
   */
  @Query("""
       SELECT pt FROM ProductTranslationEntity pt
       JOIN  pt.product p
       JOIN  pt.language l
       JOIN  ReservationEntity r ON r.id.productId = p.id
       WHERE r.id.userId = :userId
         AND l.code = :language
         AND p.status = 'VISIBLE'
      """)
  List<ProductTranslationEntity> findTranslationsForReservations(@Param("userId") Long userId, @Param("language") String language);
}
