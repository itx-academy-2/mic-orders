package com.academy.orders.application.product.usecase;

import com.academy.orders.domain.common.UrlUtils;
import com.academy.orders.domain.common.exception.BadRequestException;
import com.academy.orders.domain.discount.entity.Discount;
import com.academy.orders.domain.language.exception.LanguageNotFoundException;
import com.academy.orders.domain.language.repository.LanguageRepository;
import com.academy.orders.domain.product.dto.ProductRequestDto;
import com.academy.orders.domain.product.entity.Language;
import com.academy.orders.domain.product.entity.Product;
import com.academy.orders.domain.product.entity.ProductManagement;
import com.academy.orders.domain.product.entity.ProductTranslationManagement;
import com.academy.orders.domain.product.entity.enumerated.ProductStatus;
import com.academy.orders.domain.product.repository.ProductRepository;
import com.academy.orders.domain.product.usecase.CreateProductUseCase;
import com.academy.orders.domain.product.usecase.GetCountOfDiscountedProductsUseCase;
import com.academy.orders.domain.tag.repository.TagRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@Transactional
public class CreateProductUseCaseImpl implements CreateProductUseCase {
  private final ProductRepository productRepository;

  private final TagRepository tagRepository;

  private final LanguageRepository languageRepository;

  private final GetCountOfDiscountedProductsUseCase getCountOfDiscountedProductsUseCase;

  private final String defaultImageUrl;

  public CreateProductUseCaseImpl(ProductRepository productRepository,
      TagRepository tagRepository,
      LanguageRepository languageRepository,
      GetCountOfDiscountedProductsUseCase getCountOfDiscountedProductsUseCase,
      @Value("${images.product}") String defaultImageUrl) {
    this.productRepository = productRepository;
    this.tagRepository = tagRepository;
    this.languageRepository = languageRepository;
    this.getCountOfDiscountedProductsUseCase = getCountOfDiscountedProductsUseCase;
    this.defaultImageUrl = defaultImageUrl;
  }

  @Override
  public Product createProduct(ProductRequestDto request) {
    if (request == null) {
      throw new BadRequestException("Request cannot be null") {};
    } else if (request.image() != null && !UrlUtils.isValidUri(request.image())) {
      throw new BadRequestException("Url is not correct") {};
    } else if (request.discount() != null && !Discount.isCorrectAmount(request.discount())) {
      throw new BadRequestException("The provided discount amount is not valid. Please provide a valid discount.") {};
    } else if (request.discount() != null && getCountOfDiscountedProductsUseCase.getCountOfDiscountedProducts() >= 10) {
      throw new BadRequestException("You cannot apply discounts to more than 10 products. "
          +
          "Please remove a discount from one or more products.") {};
    }
    var tags = tagRepository.getTagsByIds(request.tagIds());

    final String imageUrl = (request.image() != null) ? request.image() : defaultImageUrl;

    var productTranslations = request.productTranslations().stream().map(dto -> {
      var language = languageRepository.findByCode(dto.languageCode())
          .orElseThrow(() -> new LanguageNotFoundException(dto.languageCode()));
      return new ProductTranslationManagement(null, language.id(), dto.name(),
          dto.description(), new Language(language.id(), dto.languageCode()));
    }).collect(Collectors.toSet());

    var product = ProductManagement.builder().status(ProductStatus.valueOf(request.status())).image(imageUrl)
        .quantity(request.quantity()).price(request.price()).tags(tags)
        .discount(request.discount()).productTranslationManagement(productTranslations).build();

    return productRepository.save(product);
  }
}
