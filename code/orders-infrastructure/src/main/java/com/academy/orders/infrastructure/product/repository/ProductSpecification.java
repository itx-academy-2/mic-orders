package com.academy.orders.infrastructure.product.repository;

import com.academy.orders.domain.product.dto.ProductFilterDto;
import com.academy.orders.infrastructure.discount.entity.DiscountEntity;
import com.academy.orders.infrastructure.language.entity.LanguageEntity;
import com.academy.orders.infrastructure.order.entity.OrderItemEntity;
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

  private final String language;

  private final List<String> sort;

  private final ProductFilterDto filter;

  private final List<UUID> bestsellersIds;

  public ProductSpecification(String language, List<String> sort, ProductFilterDto filter, List<UUID> bestsellersIds) {
    this.language = language;
    this.sort = sort;
    this.filter = filter;
    this.bestsellersIds = (bestsellersIds == null) ? List.of() : List.copyOf(bestsellersIds);
  }

  @Override
  public Predicate toPredicate(Root<ProductTranslationEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
    final List<Predicate> predicates = new ArrayList<>();

    final Join<ProductTranslationEntity, ProductEntity> productJoin = root.join("product", JoinType.LEFT);
    final Join<ProductTranslationEntity, LanguageEntity> languageJoin = root.join("language", JoinType.LEFT);
    final Join<ProductEntity, TagEntity> tagsJoin = productJoin.join("tags", JoinType.LEFT);

    predicates.add(cb.equal(productJoin.get("status"), "VISIBLE"));
    predicates.add(cb.equal(languageJoin.get("code"), language));

    // Tags
    if (filter.tags() != null && !filter.tags().isEmpty()) {
      predicates.add(tagsJoin.get("name").in(filter.tags()));
    }

    // Price range
    if (filter.priceMin() != null && filter.priceMax() != null) {
      predicates.add(cb.between(productJoin.get("price"), filter.priceMin(), filter.priceMax()));
    } else if (filter.priceMin() != null) {
      predicates.add(cb.greaterThanOrEqualTo(productJoin.get("price"), filter.priceMin()));
    } else if (filter.priceMax() != null) {
      predicates.add(cb.lessThanOrEqualTo(productJoin.get("price"), filter.priceMax()));
    }

    // Discount
    if (Boolean.TRUE.equals(filter.discount()) && !Boolean.TRUE.equals(filter.nonDiscount())) {
      predicates.add(cb.isNotNull(productJoin.get("discount")));
    } else if (Boolean.TRUE.equals(filter.nonDiscount()) && !Boolean.TRUE.equals(filter.discount())) {
      predicates.add(cb.isNull(productJoin.get("discount")));
    }

    // Availability
    if (Boolean.TRUE.equals(filter.availability()) && !Boolean.TRUE.equals(filter.nonAvailability())) {
      predicates.add(cb.greaterThan(productJoin.get("quantity"), 0));
    } else if (Boolean.TRUE.equals(filter.nonAvailability()) && !Boolean.TRUE.equals(filter.availability())) {
      predicates.add(cb.equal(productJoin.get("quantity"), 0));
    }

    setSort(root, query, cb, productJoin, languageJoin);

    return combineAllPredicates(cb, predicates);
  }

  private Predicate combineAllPredicates(final CriteriaBuilder cb, final List<Predicate> predicates) {
    return predicates.stream()
        .filter(Objects::nonNull)
        .reduce(cb.conjunction(), cb::and);
  }

  private void setSort(final Root<ProductTranslationEntity> root, final CriteriaQuery<?> query, final CriteriaBuilder cb,
      Join<ProductTranslationEntity, ProductEntity> productJoin, Join<ProductTranslationEntity, LanguageEntity> languageJoin) {
    final List<Order> orders = new ArrayList<>();

    if (sort != null && !sort.isEmpty()) {
      if (sort.size() % 2 != 0) {
        throw new IllegalArgumentException("Sort list must contain field-direction pairs (e.g. [\"price\",\"asc\"]).");
      }
      // Dynamic sorting logic
      for (int i = 0; i < sort.size(); i += 2) {
        String field = sort.get(i);
        String order = sort.get(i + 1);

        switch (field) {
          case "name" -> orders.add("asc".equalsIgnoreCase(order) ? cb.asc(root.get(field)) : cb.desc(root.get(field)));
          case "product.createdAt" -> orders
              .add("asc".equalsIgnoreCase(order) ? cb.asc(productJoin.get("createdAt")) : cb.desc(productJoin.get("createdAt")));
          case "product.price" -> {
            Expression<?> discountedPrice = buildDiscountedPriceExpression(cb, productJoin);
            orders.add("asc".equalsIgnoreCase(order) ? cb.asc(discountedPrice) : cb.desc(discountedPrice));
          }
          case "percentageOfTotalOrders" -> {
            if (!bestsellersIds.isEmpty()) {
              addBestsellerSorting(root, cb, orders, order);
            }
          }
        }
      }
    } else {
      // Default sorting logic: bestsellers first, then product ID
      Join<ProductEntity, OrderItemEntity> orderItemsJoin = productJoin.join("orderItems", JoinType.LEFT);

      query.groupBy(
          root.get("productTranslationId").get("productId"),
          root.get("productTranslationId").get("languageId"),
          productJoin.get("id"),
          languageJoin.get("id"));

      orders.add(cb.desc(cb.count(orderItemsJoin.get("orderItemId").get("productId"))));
      orders.add(cb.desc(productJoin.get("id")));
    }

    if (!orders.isEmpty()) {
      query.orderBy(orders);
    }
  }

  private void addBestsellerSorting(Root<ProductTranslationEntity> root, CriteriaBuilder cb, List<Order> orders, String order) {
    boolean ascending = "asc".equalsIgnoreCase(order);
    CriteriaBuilder.Case<Integer> caseExpr = cb.selectCase();
    for (int idx = 0; idx < bestsellersIds.size(); idx++) {
      int rank = ascending ? (bestsellersIds.size() - idx) : (bestsellersIds.size() - 1 - idx);
      caseExpr = caseExpr.when(cb.equal(root.get("product").get("id"), bestsellersIds.get(idx)), rank);
    }
    int fallback = ascending ? 0 : Integer.MIN_VALUE;
    Expression<Integer> rankingExpression = caseExpr.otherwise(fallback);
    orders.add(ascending ? cb.asc(rankingExpression) : cb.desc(rankingExpression));
  }

  private Expression<?> buildDiscountedPriceExpression(CriteriaBuilder cb, Join<ProductTranslationEntity, ProductEntity> productJoin) {
    Join<ProductEntity, DiscountEntity> discountJoin = productJoin.join("discount", JoinType.LEFT);
    var amountPct = cb.quot(cb.coalesce(discountJoin.get("amount"), cb.literal(0)), cb.literal(100.0));
    var discount = cb.prod(productJoin.get("price"), amountPct);

    return cb.coalesce(cb.diff(productJoin.get("price"), discount), productJoin.get("price"));
  }
}
