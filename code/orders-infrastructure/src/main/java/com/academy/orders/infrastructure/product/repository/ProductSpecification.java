package com.academy.orders.infrastructure.product.repository;

import com.academy.orders.infrastructure.discount.entity.DiscountEntity;
import com.academy.orders.infrastructure.language.entity.LanguageEntity;
import com.academy.orders.infrastructure.product.entity.ProductEntity;
import com.academy.orders.infrastructure.product.entity.ProductTranslationEntity;
import com.academy.orders.infrastructure.tag.entity.TagEntity;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ProductSpecification implements Specification<ProductTranslationEntity> {
  private String language;

  private List<String> sort;

  private List<String> tags;

  private List<UUID> bestsellersIds;

  public ProductSpecification(String language, List<String> sort, List<String> tags, List<UUID> bestsellersIds) {
    this.language = language;
    this.sort = sort;
    this.tags = tags;
    this.bestsellersIds = bestsellersIds;
  }

  @Override
  public Predicate toPredicate(Root<ProductTranslationEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
    final List<Predicate> predicates = new ArrayList<>();

    final Join<ProductTranslationEntity, ProductEntity> productJoin = root.join("product", JoinType.LEFT);
    final Join<ProductTranslationEntity, LanguageEntity> languageJoin = root.join("language", JoinType.LEFT);
    final Join<ProductEntity, TagEntity> tagsJoin = productJoin.join("tags", JoinType.LEFT);
    final Join<ProductEntity, DiscountEntity> discountJoin = productJoin.join("discount", JoinType.LEFT);

    predicates.add(cb.like(productJoin.get("status"), "VISIBLE"));
    predicates.add(cb.like(languageJoin.get("code"), language));

    if (tags != null && !tags.isEmpty()) {
      predicates.add(tagsJoin.get("name").in(tags));
    }

    setSort(root, query, cb, productJoin);

    return combineAllPredicates(cb, predicates);
  }

  private Predicate combineAllPredicates(final CriteriaBuilder cb, final List<Predicate> predicates) {
    return predicates.stream().reduce(cb.conjunction(), cb::and);
  }

  private void setSort(final Root<ProductTranslationEntity> root, final CriteriaQuery<?> query, final CriteriaBuilder cb,
      Join<ProductTranslationEntity, ProductEntity> productJoin) {
    if (Objects.nonNull(sort) && !sort.isEmpty()) {
      final List<Order> orders = new ArrayList<>();
      for (int i = 0; i < sort.size(); i += 2) {
        final String field = sort.get(i);
        final String order = sort.get(i + 1);
        if (field.equals("name") && order.equals("asc")) {
          orders.add(cb.asc(root.get(field)));
        } else if (field.equals("name") && order.equals("desc")) {
          orders.add(cb.desc(root.get(field)));
        }
        if (field.equals("product.createdAt") && order.equals("asc")) {
          orders.add(cb.asc(productJoin.get("createdAt")));
        } else if (field.equals("product.createdAt") && order.equals("desc")) {
          orders.add(cb.desc(productJoin.get("createdAt")));
        }

        if (field.equals("product.price") && order.equals("asc")) {
          orders.add(cb.asc(productJoin.get("price")));
        } else if (field.equals("product.price") && order.equals("desc")) {
          orders.add(cb.desc(productJoin.get("price")));
        }

        if (field.equals("percentageOfTotalOrders") && order.equals("asc")) {

          final CriteriaBuilder.Case<Integer> uuidExpression = cb.selectCase();

          for (int j = 0; j < bestsellersIds.size(); j++) {
            Expression<Integer> exp =
                uuidExpression.when(cb.equal(root.get("product").get("id"), bestsellersIds.get(j)),
                    bestsellersIds.size() - j).otherwise(Integer.MAX_VALUE);
            orders.add(cb.asc(exp));
          }
        } else if (field.equals("percentageOfTotalOrders") && order.equals("desc")) {

          final CriteriaBuilder.Case<Integer> uuidExpression = cb.selectCase();

          for (int j = 0; j < bestsellersIds.size(); j++) {
            Expression<Integer> exp =
                uuidExpression.when(cb.equal(root.get("product").get("id"), bestsellersIds.get(j)),
                    j).otherwise(Integer.MAX_VALUE);
            orders.add(cb.asc(exp));
          }
        }
      }
      if (!orders.isEmpty()) {
        query.orderBy(orders);
      }
    }
  }
}
