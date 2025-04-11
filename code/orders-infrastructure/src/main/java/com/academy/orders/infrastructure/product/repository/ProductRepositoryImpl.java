package com.academy.orders.infrastructure.product.repository;

import com.academy.orders.domain.common.Page;
import com.academy.orders.domain.common.Pageable;
import com.academy.orders.domain.product.dto.DiscountAndPriceWithDiscountRangeDto;
import com.academy.orders.domain.product.dto.ProductBestsellersDto;
import com.academy.orders.domain.product.dto.ProductLanguageDto;
import com.academy.orders.domain.product.dto.ProductManagementFilterDto;
import com.academy.orders.domain.product.dto.ProductsOnSaleFilterDto;
import com.academy.orders.domain.product.entity.Product;
import com.academy.orders.domain.product.entity.ProductManagement;
import com.academy.orders.domain.product.entity.ProductTranslationManagement;
import com.academy.orders.domain.product.entity.enumerated.ProductStatus;
import com.academy.orders.domain.product.repository.ProductRepository;
import com.academy.orders.infrastructure.common.PageableMapper;
import com.academy.orders.infrastructure.language.repository.LanguageJpaAdapter;
import com.academy.orders.infrastructure.product.ProductManagementMapper;
import com.academy.orders.infrastructure.product.ProductMapper;
import com.academy.orders.infrastructure.product.ProductPageMapper;
import com.academy.orders.infrastructure.product.ProductTranslationManagementMapper;
import com.academy.orders.infrastructure.product.entity.ProductEntity;
import com.academy.orders.infrastructure.product.entity.ProductTranslationEntity;
import jakarta.persistence.Tuple;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static java.util.Collections.emptyList;
import static java.util.Objects.isNull;

@Repository
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ProductRepositoryImpl implements ProductRepository {
  private final ProductJpaAdapter productJpaAdapter;

  private final ProductMapper productMapper;

  private final ProductManagementMapper productManagementMapper;

  private final ProductTranslationManagementMapper productTranslationManagementMapper;

  private final ProductTranslationJpaAdapter productTranslationJpaAdapter;

  private final ProductPageMapper productPageMapper;

  private final PageableMapper pageableMapper;

  private final LanguageJpaAdapter languageJpaAdapter;

  @Override
  public Page<Product> findAllProducts(String language, Pageable pageable, List<String> tags) {
    List<String> tagList = isNull(tags) ? emptyList() : tags;
    var pageableSpring = pageableMapper.fromDomain(pageable);
    var translations = productJpaAdapter.findAllByLanguageCodeAndStatusVisible(language, pageableSpring, tagList);
    List<ProductEntity> products = translations.getContent().stream().map(ProductTranslationEntity::getProduct)
        .toList();
    productJpaAdapter.findAllByIdAndLanguageCode(products.stream().map(ProductEntity::getId).toList(), language);

    return productPageMapper.fromProductTranslationEntity(translations);
  }

  @Override
  public Page<Product> findAllProducts(String language, Pageable pageable, List<String> tags, List<UUID> bestsellersIds) {
    var pageableSpring = pageableMapper.fromDomain(pageable).withSort(Sort.unsorted());
    var translations =
        productTranslationJpaAdapter.findAll(new ProductSpecification(language, pageable.sort(), tags, bestsellersIds), pageableSpring);

    return productPageMapper.fromProductTranslationEntity(translations);
  }

  @Override
  public Page<Product> findProductsWhereDiscountIsNotNull(ProductsOnSaleFilterDto filter, String language,
      Pageable pageable, List<UUID> bestsellersIds) {
    var pageableSpring = pageableMapper.fromDomain(pageable).withSort(Sort.unsorted());
    var translations = productTranslationJpaAdapter.findAll(new ProductTranslationSpecification(filter, pageable.sort(),
        language, bestsellersIds), pageableSpring);
    return productPageMapper.fromProductTranslationEntity(translations);
  }

  @Override
  public Page<Product> findAllProductsWithDefaultSorting(String language, Pageable pageable, List<String> tags) {
    List<String> tagList = isNull(tags) ? emptyList() : tags;
    var pageableSpring = pageableMapper.fromDomain(pageable);
    var productEntities = productJpaAdapter.findAllByLanguageCodeAndStatusVisibleOrderedByDefault(language,
        pageableSpring, tagList);
    productJpaAdapter.findAllByIdAndLanguageCode(
        productEntities.getContent().stream().map(ProductEntity::getId).toList(), language);

    return productPageMapper.toDomain(productEntities);
  }

  @Override
  @Transactional
  public void setNewProductQuantity(UUID productId, Integer quantity) {
    productJpaAdapter.setNewProductQuantity(productId, quantity);
  }

  @Override
  public boolean existById(UUID id) {
    return productJpaAdapter.existsById(id);
  }

  @Override
  @Transactional
  public void updateStatus(UUID productId, ProductStatus status) {
    productJpaAdapter.updateProductStatus(productId, status);
  }

  @Override
  public Set<ProductTranslationManagement> findTranslationsByProductId(UUID id) {
    var productTranslationEntity = productJpaAdapter.findTranslationsByProductId(id);
    return productTranslationManagementMapper.fromEntities(productTranslationEntity);
  }

  @Override
  @Transactional
  public void update(ProductManagement product) {
    var productEntity = productManagementMapper.toEntity(product);
    productJpaAdapter.save(productEntity);
  }

  @Override
  public Page<Product> findAllByLanguageWithFilter(String lang, @NonNull ProductManagementFilterDto filter,
      Pageable pageableDomain) {
    var pageable = pageableMapper.fromDomain(pageableDomain);
    var ids = productJpaAdapter.findProductsIdsByLangAndFilters(lang, filter, pageable);
    var products = productJpaAdapter.findProductsByIds(lang, ids.getContent(), pageable.getSort());

    return productPageMapper.toDomain(new PageImpl<>(products, pageable, ids.getTotalElements()));
  }

  @Override
  public Optional<Product> getById(UUID productId) {
    var productEntity = productJpaAdapter.findProductByProductId(productId);
    return productEntity.map(productMapper::fromEntity);
  }

  @Override
  public Product save(ProductManagement product) {
    var entity = productManagementMapper.toEntity(product);
    entity.getProductTranslations().forEach(o -> o.setLanguage(languageJpaAdapter.findByCode(o.getLanguage().getCode()).get()));
    return productMapper.fromEntity(productJpaAdapter.save(entity));
  }

  @Override
  public Page<Product> searchProductsByName(String searchQuery, String lang, Pageable pageableDomain) {
    var pageable = pageableMapper.fromDomain(pageableDomain);
    var translations = productJpaAdapter.findProductsByNameWithSearchQuery(searchQuery, lang, pageable);
    return productPageMapper.fromProductTranslationEntity(translations);
  }

  @Override
  public Optional<Product> getByIdAndLanguageCode(UUID productId, String lang) {
    var productEntity = productJpaAdapter.findProductByProductIdAndLanguageCode(productId, lang);
    return productEntity.map(productMapper::fromEntity);
  }

  @Override
  public int countByDiscountIsNotNull() {
    return productJpaAdapter.countByDiscountIsNotNull();
  }

  @Override
  public DiscountAndPriceWithDiscountRangeDto findDiscountAndPriceWithDiscountRange() {
    var tuple = productJpaAdapter.findDiscountAndPriceWithDiscountRange();
    return DiscountAndPriceWithDiscountRangeDto.builder()
        .minimumPriceWithDiscount(tuple.get(0, BigDecimal.class))
        .maximumPriceWithDiscount(tuple.get(1, BigDecimal.class))
        .minimumDiscount(tuple.get(2, Integer.class))
        .maximumDiscount(tuple.get(3, Integer.class))
        .build();
  }

  @Override
  public Page<Product> findProductsByLanguageAndIds(Pageable pageableDomain, String language, List<UUID> ids,
      List<ProductLanguageDto> languageDtos) {
    var pageable = pageableMapper.fromDomain(pageableDomain);
    var translations = productTranslationJpaAdapter.findAll(new ProductLanguageSpecification(language, ids, languageDtos), pageable);
    return productPageMapper.fromProductTranslationEntity(translations);
  }

  @Override
  public List<ProductBestsellersDto> getIdsOfMostSoldProducts(LocalDateTime fromDate, LocalDateTime endDate, int quantity) {
    List<Tuple> tuple = productJpaAdapter.findMostSoldProducts(fromDate, endDate, quantity);
    return tuple.stream()
        .map(o -> {
          final UUID productId = o.get(0, UUID.class);
          final Double percentageOfTotalOrders = o.get(1, Double.class);
          return new ProductBestsellersDto(productId, percentageOfTotalOrders);
        }).toList();
  }

  @Override
  public List<ProductLanguageDto> getProductLanguagesDto(String lang, List<UUID> ids) {
    final List<Tuple> tuples = productJpaAdapter.getProductLanguagesDto(lang, ids);
    return tuples.stream()
        .map(o -> {
          final UUID productId = o.get(0, UUID.class);
          final String language = o.get(1, String.class);
          return new ProductLanguageDto(productId, language);
        }).toList();
  }
}
