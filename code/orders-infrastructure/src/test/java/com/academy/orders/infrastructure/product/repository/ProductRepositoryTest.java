package com.academy.orders.infrastructure.product.repository;

import com.academy.orders.domain.common.Page;
import com.academy.orders.domain.common.Pageable;
import com.academy.orders.domain.product.dto.ProductBestsellersDto;
import com.academy.orders.domain.product.dto.ProductLanguageDto;
import com.academy.orders.domain.product.entity.Product;
import com.academy.orders.domain.product.entity.enumerated.ProductStatus;
import com.academy.orders.infrastructure.ModelUtils;
import com.academy.orders.infrastructure.common.PageableMapper;
import com.academy.orders.infrastructure.language.repository.LanguageJpaAdapter;
import com.academy.orders.infrastructure.product.ProductManagementMapper;
import com.academy.orders.infrastructure.product.ProductMapper;
import com.academy.orders.infrastructure.product.ProductPageMapper;
import com.academy.orders.infrastructure.product.ProductTranslationManagementMapper;
import com.academy.orders.infrastructure.product.entity.ProductEntity;
import com.academy.orders.infrastructure.product.entity.ProductTranslationEntity;
import jakarta.persistence.Tuple;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

import static com.academy.orders.infrastructure.ModelUtils.*;
import static com.academy.orders.infrastructure.TestConstants.LANGUAGE_EN;
import static com.academy.orders.infrastructure.TestConstants.TEST_UUID;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.of;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductRepositoryTest {
  @InjectMocks
  private ProductRepositoryImpl productRepository;

  @Mock
  private ProductJpaAdapter productJpaAdapter;

  @Mock
  private ProductMapper productMapper;

  @Mock
  private ProductPageMapper productPageMapper;

  @Mock
  private PageableMapper pageableMapper;

  @Mock
  private LanguageJpaAdapter languageJpaAdapter;

  @Mock
  private ProductTranslationJpaAdapter productTranslationJpaAdapter;

  @Mock
  private ProductTranslationManagementMapper productTranslationManagementMapper;

  @Mock
  private ProductManagementMapper productManagementMapper;

  static Stream<Arguments> findAllProductsMethodSourceProvider() {
    return Stream.of(of(emptyList(), emptyList()), of(null, emptyList()),
        of(singletonList("category:computer"), singletonList("category:computer")));
  }

  @ParameterizedTest
  @CsvSource({"true", "false"})
  void existsByIdTest(Boolean response) {

    when(productJpaAdapter.existsById(any(UUID.class))).thenReturn(response);

    assertEquals(response, productRepository.existById(UUID.randomUUID()));
    verify(productJpaAdapter).existsById(any(UUID.class));
  }

  @Test
  void setNewProductQuantityTest() {
    doNothing().when(productJpaAdapter).setNewProductQuantity(any(UUID.class), anyInt());
    assertDoesNotThrow(() -> productRepository.setNewProductQuantity(UUID.randomUUID(), 1));
  }

  @ParameterizedTest
  @MethodSource("findAllProductsMethodSourceProvider")
  void findAllProductsTest(List<String> tagsParams, List<String> mockedTags) {
    var pageable = getPageable();
    var productTranslationEntity = getProductTranslationEntity();
    var productEntity = getProductEntity();
    var product = getProduct();
    var pageDomain = getPageOf(product);
    var page = getPageImplOf(productTranslationEntity);
    var pageRequest = getPageRequest();

    when(pageableMapper.fromDomain(pageable)).thenReturn(pageRequest);
    when(productJpaAdapter.findAllByLanguageCodeAndStatusVisible(LANGUAGE_EN, pageRequest, mockedTags))
        .thenReturn(page);
    when(productJpaAdapter.findAllByIdAndLanguageCode(List.of(productTranslationEntity.getProduct().getId()),
        LANGUAGE_EN)).thenReturn(List.of(productEntity));
    when(productPageMapper.fromProductTranslationEntity(page)).thenReturn(pageDomain);

    var products = productRepository.findAllProducts(LANGUAGE_EN, pageable, tagsParams);

    assertEquals(pageDomain, products);
    verify(pageableMapper).fromDomain(pageable);
    verify(productJpaAdapter).findAllByLanguageCodeAndStatusVisible(LANGUAGE_EN, pageRequest, mockedTags);
    verify(productJpaAdapter).findAllByIdAndLanguageCode(List.of(productTranslationEntity.getProduct().getId()),
        LANGUAGE_EN);
    verify(productPageMapper).fromProductTranslationEntity(page);
  }

  @ParameterizedTest
  @MethodSource("findAllProductsMethodSourceProvider")
  void findAllProductsWithDefaultSortingTest(List<String> tagsParams, List<String> mockedTags) {
    var pageable = getPageable();
    var productEntity = getProductEntity();
    var product = getProduct();
    var pageDomain = getPageOf(product);
    var page = getPageImplOf(productEntity);
    var pageRequest = getPageRequest();

    when(pageableMapper.fromDomain(pageable)).thenReturn(pageRequest);
    when(productJpaAdapter.findAllByLanguageCodeAndStatusVisibleOrderedByDefault(LANGUAGE_EN, pageRequest,
        mockedTags)).thenReturn(page);
    when(productJpaAdapter.findAllByIdAndLanguageCode(List.of(productEntity.getId()), LANGUAGE_EN))
        .thenReturn(List.of(productEntity));
    when(productPageMapper.toDomain(page)).thenReturn(pageDomain);
    var products = productRepository.findAllProductsWithDefaultSorting(LANGUAGE_EN, pageable, tagsParams);

    assertEquals(pageDomain, products);
    verify(pageableMapper).fromDomain(pageable);
    verify(productJpaAdapter).findAllByLanguageCodeAndStatusVisibleOrderedByDefault(LANGUAGE_EN, pageRequest,
        mockedTags);
    verify(productJpaAdapter).findAllByIdAndLanguageCode(List.of(productEntity.getId()), LANGUAGE_EN);
    verify(productPageMapper).toDomain(page);
  }

  @Test
  void updateStatusTest() {
    UUID productId = TEST_UUID;
    ProductStatus status = ProductStatus.VISIBLE;

    doNothing().when(productJpaAdapter).updateProductStatus(productId, status);
    productRepository.updateStatus(productId, status);
    verify(productJpaAdapter).updateProductStatus(productId, status);
  }

  @Test
  void findTranslationsByProductIdTest() {
    UUID productId = UUID.randomUUID();
    var productTranslationEntity = getProductTranslationEntity();
    var productTranslationManagement = getProductTranslationManagement();

    when(productJpaAdapter.findTranslationsByProductId(productId)).thenReturn(Set.of(productTranslationEntity));
    when(productTranslationManagementMapper.fromEntities(Set.of(productTranslationEntity)))
        .thenReturn(Set.of(productTranslationManagement));

    var result = productRepository.findTranslationsByProductId(productId);
    assertEquals(Set.of(productTranslationManagement), result);

    verify(productJpaAdapter).findTranslationsByProductId(productId);
    verify(productTranslationManagementMapper).fromEntities(Set.of(productTranslationEntity));
  }

  @Test
  void updateTest() {
    var productManagement = getProductManagement();
    var productEntity = getProductEntity();

    when(productManagementMapper.toEntity(productManagement)).thenReturn(productEntity);
    doAnswer(invocation -> null).when(productJpaAdapter).save(productEntity);

    productRepository.update(productManagement);

    verify(productManagementMapper).toEntity(productManagement);
    verify(productJpaAdapter).save(productEntity);
  }

  @Test
  @SuppressWarnings("unchecked")
  void findAllByLanguageWithFilterTest() {
    var filter = getManagementFilterDto();
    var pageableDomain = getPageable();
    var lang = "en";
    var sort = Sort.by(String.join(",", pageableDomain.sort()));
    var pageable = PageRequest.of(pageableDomain.page(), pageableDomain.size(), sort);
    var productEntity = getProductEntity();
    var product = getProduct();
    var ids = getPageImplOf(productEntity.getId());
    var productPage = getPageOf(product);

    when(pageableMapper.fromDomain(pageableDomain)).thenReturn(pageable);
    when(productJpaAdapter.findProductsIdsByLangAndFilters(lang, filter, pageable)).thenReturn(ids);
    when(productJpaAdapter.findProductsByIds(lang, ids.getContent(), pageable.getSort()))
        .thenReturn(singletonList(productEntity));
    when(productPageMapper.toDomain(any(PageImpl.class))).thenReturn(productPage);

    var actual = productRepository.findAllByLanguageWithFilter(lang, filter, pageableDomain);
    assertEquals(productPage, actual);

    verify(pageableMapper).fromDomain(pageableDomain);
    verify(productJpaAdapter).findProductsIdsByLangAndFilters(lang, filter, pageable);
    verify(productJpaAdapter).findProductsByIds(lang, ids.getContent(), pageable.getSort());
    verify(productPageMapper).toDomain(any(PageImpl.class));

  }

  @Test
  void saveTest() {
    var productManagement = getProductManagement();
    var productEntity = getProductEntityWithProductTranslations();
    var product = getProduct();
    final List<String> languageCodes = productEntity.getProductTranslations().stream()
        .map(o -> o.getLanguage().getCode())
        .toList();

    when(productManagementMapper.toEntity(productManagement)).thenReturn(productEntity);
    when(productJpaAdapter.save(productEntity)).thenReturn(productEntity);
    when(productMapper.fromEntity(productEntity)).thenReturn(product);
    languageCodes
        .forEach(code -> when(languageJpaAdapter.findByCode(code)).thenReturn(Optional.of(getLanguageEntity(code))));

    var result = productRepository.save(productManagement);

    assertNotNull(result);
    assertEquals(product, result);

    verify(productManagementMapper).toEntity(productManagement);
    verify(productJpaAdapter).save(productEntity);
    verify(productMapper).fromEntity(productEntity);
    languageCodes
        .forEach(code -> verify(languageJpaAdapter).findByCode(code));
  }

  @Test
  void getByIdTest() {
    var product = getProduct();
    var productEntity = getProductEntity();

    when(productJpaAdapter.findProductByProductId(TEST_UUID)).thenReturn(Optional.of(productEntity));
    when(productMapper.fromEntity(productEntity)).thenReturn(product);

    var result = productRepository.getById(TEST_UUID);
    assertEquals(result, Optional.of(product));

    verify(productJpaAdapter).findProductByProductId(TEST_UUID);
    verify(productMapper).fromEntity(productEntity);
  }

  @Test
  void searchProductsByNameTest() {
    // Given
    var pageableDomain = getPageable();
    var pageable = PageRequest.of(0, 7);
    var entityPage = ModelUtils.getPageImplOf(getProductTranslationEntity());
    var page = ModelUtils.getPageOf(getProduct());
    String searchQuery = "some text";
    String lang = "en";

    when(pageableMapper.fromDomain(pageableDomain)).thenReturn(pageable);
    when(productJpaAdapter.findProductsByNameWithSearchQuery(searchQuery, lang, pageable)).thenReturn(entityPage);
    when(productPageMapper.fromProductTranslationEntity(entityPage)).thenReturn(page);

    // When
    Page<Product> actual = productRepository.searchProductsByName(searchQuery, lang, pageableDomain);

    // Then
    assertEquals(page, actual);
    verify(pageableMapper).fromDomain(pageableDomain);
    verify(productJpaAdapter).findProductsByNameWithSearchQuery(searchQuery, lang, pageable);
    verify(productPageMapper).fromProductTranslationEntity(entityPage);
  }

  @Test
  void getByIdAndLanguageCodeTest() {
    var product = getProduct();
    var productEntity = getProductEntityWithTranslation();

    when(productJpaAdapter.findProductByProductIdAndLanguageCode(TEST_UUID, LANGUAGE_EN))
        .thenReturn(Optional.of(productEntity));
    when(productMapper.fromEntity(productEntity)).thenReturn(product);

    var result = productRepository.getByIdAndLanguageCode(TEST_UUID, LANGUAGE_EN);
    assertEquals(result, Optional.of(product));

    verify(productJpaAdapter).findProductByProductIdAndLanguageCode(TEST_UUID, LANGUAGE_EN);
    verify(productMapper).fromEntity(productEntity);
  }

  @Test
  void countByDiscountIsNotNullTest() {
    var value = 3;

    when(productJpaAdapter.countByDiscountIsNotNull()).thenReturn(value);

    var result = productRepository.countByDiscountIsNotNull();
    assertEquals(value, result);

    verify(productJpaAdapter).countByDiscountIsNotNull();
  }

  @Test
  void findDiscountAndPriceWithDiscountRangeTest() {
    final BigDecimal minimumPriceWithDiscount = BigDecimal.valueOf(100.00);
    final BigDecimal maximumPriceWithDiscount = BigDecimal.valueOf(955.50);
    final Integer minimumDiscount = 10;
    final Integer maximumDiscount = 30;

    final Tuple tuple = mock(Tuple.class);
    when(tuple.get(0, BigDecimal.class)).thenReturn(minimumPriceWithDiscount);
    when(tuple.get(1, BigDecimal.class)).thenReturn(maximumPriceWithDiscount);
    when(tuple.get(2, Integer.class)).thenReturn(minimumDiscount);
    when(tuple.get(3, Integer.class)).thenReturn(maximumDiscount);

    when(productJpaAdapter.findDiscountAndPriceWithDiscountRange()).thenReturn(tuple);

    var result = productRepository.findDiscountAndPriceWithDiscountRange();

    assertNotNull(result);
    assertEquals(minimumPriceWithDiscount, result.minimumPriceWithDiscount());
    assertEquals(maximumPriceWithDiscount, result.maximumPriceWithDiscount());
    assertEquals(minimumDiscount, result.minimumDiscount());
    assertEquals(maximumDiscount, result.maximumDiscount());

    verify(productJpaAdapter).findDiscountAndPriceWithDiscountRange();
  }

  @Test
  void findProductsByLanguageAndIdsTest() {
    final Pageable pageableDomain = Pageable.builder().page(1).size(1).build();
    final PageRequest pageable = PageRequest.of(1, 1);
    final ProductEntity productEntity = getProductEntity();
    final ProductTranslationEntity productTranslationEntity = getProductTranslationEntity();
    final List<UUID> ids = List.of(productEntity.getId());
    final List<ProductLanguageDto> languageDtos = List.of(new ProductLanguageDto(TEST_UUID, LANGUAGE_EN));
    var productPage = getPageImplOf(productTranslationEntity);

    when(pageableMapper.fromDomain(pageableDomain)).thenReturn(pageable);
    when(productTranslationJpaAdapter.findAll(any(ProductLanguageSpecification.class), eq(pageable)))
        .thenReturn(productPage);

    productRepository.findProductsByLanguageAndIds(pageableDomain, LANGUAGE_EN, ids, languageDtos);

    verify(pageableMapper).fromDomain(pageableDomain);
    verify(productTranslationJpaAdapter).findAll(any(ProductLanguageSpecification.class), eq(pageable));
    verify(productPageMapper).fromProductTranslationEntity(productPage);
  }

  @Test
  void getProductLanguagesDtoTest() {
    final UUID uuid = TEST_UUID;
    final String language = LANGUAGE_EN;
    final List<UUID> ids = List.of(uuid);
    final Tuple tuple = mock(Tuple.class);
    final List<Tuple> tuples = List.of(tuple);
    final ProductLanguageDto expected = new ProductLanguageDto(uuid, language);

    when(tuple.get(0, UUID.class)).thenReturn(uuid);
    when(tuple.get(1, String.class)).thenReturn(language);
    when(productJpaAdapter.getProductLanguagesDto(language, ids))
        .thenReturn(tuples);

    final List<ProductLanguageDto> result = productRepository.getProductLanguagesDto(language, ids);

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(expected.id(), result.get(0).id());
    assertEquals(expected.code(), result.get(0).code());

    verify(productJpaAdapter).getProductLanguagesDto(language, ids);
    verify(tuple).get(0, UUID.class);
    verify(tuple).get(1, String.class);
  }

  @Test
  void getIdsOfMostSoldProducts() {
    final LocalDateTime now = LocalDateTime.now();
    final LocalDateTime before = now.minusDays(1);
    final Double percentageOfTotalOrders = 40.0;
    final Tuple tuple = mock(Tuple.class);
    final List<Tuple> tuples = List.of(tuple);

    when(tuple.get(0, UUID.class)).thenReturn(UUID.randomUUID());
    when(tuple.get(1, Double.class)).thenReturn(percentageOfTotalOrders);
    when(productJpaAdapter.findMostSoldProducts(before, now, tuples.size())).thenReturn(tuples);

    final List<ProductBestsellersDto> result = productRepository.getIdsOfMostSoldProducts(before, now, tuples.size());

    verify(productJpaAdapter).findMostSoldProducts(before, now, tuples.size());
    verify(tuple).get(0, UUID.class);
    verify(tuple).get(1, Double.class);

    result.forEach(dto -> {
      assertEquals(tuple.get(0, UUID.class), dto.productId());
      assertEquals(tuple.get(1, Double.class), dto.percentageOfTotalOrders());
    });
  }
}
