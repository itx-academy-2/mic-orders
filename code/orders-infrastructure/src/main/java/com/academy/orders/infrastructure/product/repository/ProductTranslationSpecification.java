package com.academy.orders.infrastructure.product.repository;

import com.academy.orders.domain.product.dto.ProductsOnSaleFilterDto;
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
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@AllArgsConstructor
public class ProductTranslationSpecification implements Specification<ProductTranslationEntity> {
  private final ProductsOnSaleFilterDto filter;

  private final List<String> sort;

  private String language;

  @Override
  public Predicate toPredicate(final Root<ProductTranslationEntity> root, final CriteriaQuery<?> query,
      final CriteriaBuilder cb) {
    final List<Predicate> predicates = new ArrayList<>();

    final Join<ProductTranslationEntity, LanguageEntity> languageJoin = root.join("language", JoinType.INNER);
    final Join<ProductTranslationEntity, ProductEntity> productJoin = root.join("product", JoinType.INNER);
    final Join<ProductEntity, DiscountEntity> discountJoin = productJoin.join("discount", JoinType.INNER);
    final Join<ProductEntity, TagEntity> tagJoin = productJoin.join("tags", JoinType.INNER);

    if (Objects.nonNull(filter.tags()) && !filter.tags().isEmpty()) {
      predicates.add(tagJoin.get("name").in(filter.tags()));
    }

    if (Objects.nonNull(filter.minimumDiscount())) {
      predicates.add(cb.greaterThanOrEqualTo(discountJoin.get("amount"), filter.minimumDiscount()));
    }
    if (Objects.nonNull(filter.maximumDiscount())) {
      predicates.add(cb.lessThanOrEqualTo(discountJoin.get("amount"), filter.maximumDiscount()));
    }

    final Expression<Number> discount = cb.quot(discountJoin.get("amount"), cb.literal(100));
    final Expression<Number> discountMultiplier = cb.diff(cb.literal(BigDecimal.ONE), discount);
    final Expression<Number> priceWithDiscount = cb.prod(productJoin.get("price"), discountMultiplier);

    if (Objects.nonNull(filter.minimumPriceWithDiscount())) {
      predicates.add(cb.greaterThanOrEqualTo(priceWithDiscount.as(BigDecimal.class),
          filter.minimumPriceWithDiscount()));
    }
    if (Objects.nonNull(filter.maximumPriceWithDiscount())) {
      predicates.add(cb.lessThanOrEqualTo(priceWithDiscount.as(BigDecimal.class),
          filter.maximumPriceWithDiscount()));
    }

    predicates.add(cb.like(productJoin.get("status"), "VISIBLE"));
    predicates.add(cb.like(languageJoin.get("code"), language));

    setSort(root, query, cb, discountJoin, productJoin, priceWithDiscount);

    return combineAllPredicates(cb, predicates);
  }

  private Predicate combineAllPredicates(final CriteriaBuilder cb, final List<Predicate> predicates) {
    return predicates.stream().reduce(cb.conjunction(), cb::and);
  }

  private void setSort(final Root<ProductTranslationEntity> root, final CriteriaQuery<?> query, final CriteriaBuilder cb,
      final Join<ProductEntity, DiscountEntity> discountJoin,
      final Join<ProductTranslationEntity, ProductEntity> productJoin,
      final Expression<Number> priceWithDiscount) {
    if (Objects.nonNull(sort) && !sort.isEmpty()) {
      final List<Order> orders = new ArrayList<>();
      for (int i = 0; i < sort.size(); i += 2) {
        final String field = sort.get(i);
        final String order = sort.get(i + 1);
        if (field.equals("discount") && order.equals("asc")) {
          orders.add(cb.asc(discountJoin.get("amount")));
        } else if (field.equals("discount") && order.equals("desc")) {
          orders.add(cb.desc(discountJoin.get("amount")));
        }
        if (field.equals("priceWithDiscount") && order.equals("asc")) {
          orders.add(cb.asc(priceWithDiscount));
        } else if (field.equals("priceWithDiscount") && order.equals("desc")) {
          orders.add(cb.desc(priceWithDiscount));
        }
        if (field.equals("name") && order.equals("asc")) {
          orders.add(cb.asc(root.get("name")));
        } else if (field.equals("name") && order.equals("desc")) {
          orders.add(cb.desc(root.get("name")));
        }

        if (!orders.isEmpty()) {
          query.orderBy(orders);
        }
      }
    }
  }
}
