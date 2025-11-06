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
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static com.academy.orders.infrastructure.ModelUtils.getProductFilterDto;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ProductSpecificationTest {

  @Mock
  private Root<ProductTranslationEntity> root;

  @Mock
  private CriteriaQuery<?> query;

  @Mock
  private CriteriaBuilder cb;

  @Mock
  private Join<ProductTranslationEntity, ProductEntity> productJoin;

  @Mock
  private Join<ProductTranslationEntity, LanguageEntity> languageJoin;

  @Mock
  private Join<ProductEntity, TagEntity> tagsJoin;

  @Mock
  private Join<ProductEntity, DiscountEntity> discountJoin;

  @Mock
  private Join<ProductEntity, OrderItemEntity> orderItemsJoin;

  @Mock
  private Predicate predicate;

  @Mock(answer = Answers.RETURNS_SELF)
  private CriteriaBuilder.Case<Integer> caseMock;

  private ProductSpecification spec;

  private final String language = "en";

  private List<String> sort = new ArrayList<>();

  private final List<UUID> bestsellersIds = List.of(UUID.randomUUID(), UUID.randomUUID());

  private final ProductFilterDto filterDto = getProductFilterDto();

  @BeforeEach
  void setUp() {
    spec = new ProductSpecification(language, sort, filterDto, bestsellersIds);
    sort.clear();

    lenient().when(root.join(eq("product"), eq(JoinType.LEFT))).thenReturn((Join) productJoin);
    lenient().when(root.join(eq("language"), eq(JoinType.LEFT))).thenReturn((Join) languageJoin);
    lenient().when(root.get("productTranslationId")).thenReturn(mock(Path.class));
    lenient().when(root.get("productId")).thenReturn(mock(Path.class));
    lenient().when(root.get("languageId")).thenReturn(mock(Path.class));
    lenient().when(productJoin.get("id")).thenReturn(mock(Path.class));
    lenient().when(productJoin.join(eq("tags"), eq(JoinType.LEFT))).thenReturn((Join) tagsJoin);
    lenient().when(productJoin.join(eq("discount"), eq(JoinType.LEFT))).thenReturn((Join) discountJoin);
    lenient().when(productJoin.join(eq("orderItems"), eq(JoinType.LEFT))).thenReturn((Join) orderItemsJoin);
    lenient().when(productJoin.get("status")).thenReturn(mock(Path.class));
    lenient().when(languageJoin.get("code")).thenReturn(mock(Path.class));
    lenient().when(languageJoin.get("id")).thenReturn(mock(Path.class));
    lenient().when(tagsJoin.get("name")).thenReturn(mock(Path.class));
    lenient().when(productJoin.get("createdAt")).thenReturn(mock(Path.class));
    lenient().when(productJoin.get("price")).thenReturn(mock(Path.class));
    lenient().when(discountJoin.get("amount")).thenReturn(mock(Path.class));
    lenient().when(orderItemsJoin.get("orderItemId")).thenReturn(mock(Path.class));
    lenient().when(orderItemsJoin.get("productId")).thenReturn(mock(Path.class));
    lenient().when(root.get("product")).thenReturn(mock(Path.class));
    lenient().when(cb.like(any(Expression.class), anyString())).thenReturn(predicate);
    lenient().when(cb.conjunction()).thenReturn(predicate);
    lenient().when(cb.and(any(Predicate.class), any(Predicate.class))).thenAnswer(invocation -> predicate);
    lenient().when(cb.asc(any(Expression.class))).thenReturn(mock(Order.class));
    lenient().when(cb.desc(any(Expression.class))).thenReturn(mock(Order.class));
    lenient().when(cb.diff(any(Expression.class), any(Expression.class))).thenReturn(mock(Expression.class));
    lenient().when(cb.prod(any(Expression.class), any(Expression.class))).thenReturn(mock(Expression.class));
    lenient().when(cb.quot(any(Expression.class), any(Double.class))).thenReturn(mock(Expression.class));
    lenient().when(cb.coalesce(any(Expression.class), any(Expression.class))).thenReturn(mock(Expression.class));
    lenient().when(cb.toDouble(any(Expression.class))).thenReturn(mock(Expression.class));
    lenient().when(cb.count(any(Expression.class))).thenReturn(mock(Expression.class));
    lenient().doReturn(caseMock).when(cb).selectCase();
    lenient().doReturn(mock(Expression.class)).when(caseMock).otherwise(any(Integer.class));
  }

  @Test
  void toPredicateWithFiltersAllTrueAndSortTest() {
    // Given
    sort.add("product.price");
    sort.add("asc");
    ProductSpecification spec = new ProductSpecification(language, sort, filterDto, bestsellersIds);

    // When
    Predicate predicateResult = spec.toPredicate(root, query, cb);

    // Then
    assertNotNull(predicateResult);
    verify(root).join("product", JoinType.LEFT);
    verify(root).join("language", JoinType.LEFT);
    verify(productJoin).join("tags", JoinType.LEFT);
    verify(productJoin).join("discount", JoinType.LEFT);
    verify(cb).equal(any(), eq("VISIBLE"));
    verify(cb).equal(any(), eq(language));
  }

  @Test
  void toPredicateWithFilterDiscountTrueAvailabilityTruePriceMinNullSortNullAndBestsellersNullTest() {
    // Given
    ProductFilterDto customFilter = ProductFilterDto.builder()
        .tags(singletonList("category:mobile"))
        .discount(true)
        .nonDiscount(false)
        .priceMin(null)
        .priceMax(BigDecimal.valueOf(1000))
        .availability(true)
        .nonAvailability(false)
        .build();
    ProductSpecification spec = new ProductSpecification(language, null, customFilter, null);

    // When
    Predicate predicateResult = spec.toPredicate(root, query, cb);

    // Then
    assertNotNull(predicateResult);
    verify(root).join("product", JoinType.LEFT);
    verify(root).join("language", JoinType.LEFT);
    verify(productJoin).join("tags", JoinType.LEFT);
    verify(cb).equal(any(), eq("VISIBLE"));
    verify(cb).equal(any(), eq(language));
    verify(cb).lessThanOrEqualTo(productJoin.get("price"), customFilter.priceMax());
    verify(cb).isNotNull(productJoin.get("discount"));
    verify(cb).greaterThan(productJoin.get("quantity"), 0);
  }

  @Test
  void toPredicateWithFilterWithNullFieldsTest() {
    // Given
    ProductFilterDto customFilter = ProductFilterDto.builder()
        .tags(null)
        .discount(null)
        .nonDiscount(null)
        .priceMin(null)
        .priceMax(null)
        .availability(null)
        .nonAvailability(null)
        .build();
    ProductSpecification spec = new ProductSpecification(language, sort, customFilter, null);

    // When
    Predicate predicateResult = spec.toPredicate(root, query, cb);

    // Then
    assertNotNull(predicateResult);
    verify(root).join("product", JoinType.LEFT);
    verify(root).join("language", JoinType.LEFT);
    verify(productJoin).join("tags", JoinType.LEFT);
    verify(cb).equal(any(), eq("VISIBLE"));
    verify(cb).equal(any(), eq(language));
  }

  @Test
  void toPredicateWithFilterNonDiscountFalseNonAvailabilityFalsePriceMaxNullEmptyTagsAndSortTest() {
    // Given
    sort.add("product.price");
    sort.add("asc");
    ProductFilterDto customFilter = ProductFilterDto.builder()
        .tags(Collections.emptyList())
        .discount(false)
        .nonDiscount(true)
        .priceMin(BigDecimal.valueOf(100))
        .priceMax(null)
        .availability(false)
        .nonAvailability(true)
        .build();
    ProductSpecification spec = new ProductSpecification(language, sort, customFilter, bestsellersIds);

    // When
    Predicate predicateResult = spec.toPredicate(root, query, cb);

    // Then
    assertNotNull(predicateResult);
    verify(root).join("product", JoinType.LEFT);
    verify(root).join("language", JoinType.LEFT);
    verify(productJoin).join("tags", JoinType.LEFT);
    verify(productJoin).join("discount", JoinType.LEFT);
    verify(cb).equal(any(), eq("VISIBLE"));
    verify(cb).equal(any(), eq(language));
    verify(cb).greaterThanOrEqualTo(productJoin.get("price"), customFilter.priceMin());
    verify(cb).isNull(productJoin.get("discount"));
    verify(cb).equal(productJoin.get("quantity"), 0);
  }

  @Test
  void sortByPriceDescendingTest() {
    // Given
    sort.add("product.price");
    sort.add("desc");

    // When
    Predicate predicateResult = spec.toPredicate(root, query, cb);

    // Then
    assertNotNull(predicateResult);
    verify(cb).desc(any());
  }

  @Test
  void sortByNameAscendingTest() {
    // Given
    sort.add("name");
    sort.add("asc");

    // When
    Predicate predicateResult = spec.toPredicate(root, query, cb);

    // Then
    assertNotNull(predicateResult);
    verify(cb).asc(any());
  }

  @Test
  void sortByNameDescendingTest() {
    // Given
    sort.add("name");
    sort.add("desc");

    // When
    Predicate predicateResult = spec.toPredicate(root, query, cb);

    // Then
    assertNotNull(predicateResult);
    verify(cb).desc(any());
  }

  @Test
  void sortByProductCreatedAtAscendingTest() {
    // Given
    sort.add("product.createdAt");
    sort.add("asc");

    // When
    Predicate predicateResult = spec.toPredicate(root, query, cb);

    // Then
    assertNotNull(predicateResult);
    verify(cb).asc(any());
  }

  @Test
  void sortByProductCreatedAtDescendingTest() {
    // Given
    sort.add("product.createdAt");
    sort.add("desc");

    // When
    Predicate predicateResult = spec.toPredicate(root, query, cb);

    // Then
    assertNotNull(predicateResult);
    verify(cb).desc(any());
  }

  @Test
  void sortByPercentageOfTotalOrdersAscendingTest() {
    // Given
    sort.add("percentageOfTotalOrders");
    sort.add("asc");

    // When
    Predicate predicateResult = spec.toPredicate(root, query, cb);

    // Then
    assertNotNull(predicateResult);
    verify(cb, atLeast(bestsellersIds.size())).asc(any());
  }

  @Test
  void sortByPercentageOfTotalOrdersDescendingTest() {
    // Given
    sort.add("percentageOfTotalOrders");
    sort.add("desc");

    // When
    Predicate predicateResult = spec.toPredicate(root, query, cb);

    // Then
    assertNotNull(predicateResult);
    verify(cb, atLeast(bestsellersIds.size())).asc(any());
  }

  @Test
  void sortByUnknownFieldTest() {
    // Given
    sort.add("unknownField");
    sort.add("desc");

    // When
    Predicate predicateResult = spec.toPredicate(root, query, cb);

    // Then
    assertNotNull(predicateResult);
  }

  @Test
  void emptySortListTest() {
    // Given
    sort.clear();

    // When
    Predicate predicateResult = spec.toPredicate(root, query, cb);

    // Then
    assertNotNull(predicateResult);
  }
}
