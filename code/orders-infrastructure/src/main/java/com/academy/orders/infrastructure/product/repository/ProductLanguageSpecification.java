package com.academy.orders.infrastructure.product.repository;

import com.academy.orders.domain.product.dto.ProductLanguageDto;
import com.academy.orders.infrastructure.discount.entity.DiscountEntity;
import com.academy.orders.infrastructure.language.entity.LanguageEntity;
import com.academy.orders.infrastructure.product.entity.ProductEntity;
import com.academy.orders.infrastructure.product.entity.ProductTranslationEntity;
import com.academy.orders.infrastructure.tag.entity.TagEntity;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
public class ProductLanguageSpecification implements Specification<ProductTranslationEntity> {
  private String language;

  private List<UUID> ids;

  private List<ProductLanguageDto> productLanguageDtos;

  @Override
  public Predicate toPredicate(Root<ProductTranslationEntity> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
    final List<Predicate> predicates = new ArrayList<>();

    final Join<ProductTranslationEntity, LanguageEntity> languageJoin = root.join("language", JoinType.INNER);
    final Join<ProductTranslationEntity, ProductEntity> productJoin = root.join("product", JoinType.INNER);
    final Join<ProductEntity, DiscountEntity> discountJoin = productJoin.join("discount", JoinType.LEFT);
    final Join<ProductEntity, TagEntity> tagJoin = productJoin.join("tags", JoinType.LEFT);

    predicates.add(criteriaBuilder.and(root.get("productTranslationId").get("productId").in(ids),
        criteriaBuilder.equal(languageJoin.get("code"), language)));
    predicates.add(getWhereLanguagePredicates(criteriaBuilder, root, languageJoin));
    return combineAllPredicates(criteriaBuilder, predicates);
  }

  private Predicate combineAllPredicates(final CriteriaBuilder cb, final List<Predicate> predicates) {
    return predicates.stream().reduce(cb.disjunction(), cb::or);
  }

  private Predicate getWhereLanguagePredicates(CriteriaBuilder criteriaBuilder, Root<ProductTranslationEntity> root,
      Join<ProductTranslationEntity, LanguageEntity> languageEntityJoin) {
    List<Predicate> languagePredicates = new ArrayList<>();

    for (final ProductLanguageDto dto : productLanguageDtos) {
      final Predicate idPredicate = criteriaBuilder.equal(root.get("productTranslationId").get("productId"), dto.id());

      final Predicate languagePredicate = criteriaBuilder.equal(languageEntityJoin.get("code"), dto.code());

      languagePredicates.add(criteriaBuilder.and(idPredicate, languagePredicate));
    }
    return combineAllPredicates(criteriaBuilder, languagePredicates);
  }
}
