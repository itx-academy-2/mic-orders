package com.academy.orders.application.product.usecase;

import com.academy.orders.application.ModelUtils;
import com.academy.orders.domain.common.exception.BadRequestException;
import com.academy.orders.domain.language.exception.LanguageNotFoundException;
import com.academy.orders.domain.language.repository.LanguageRepository;
import com.academy.orders.domain.product.entity.ProductManagement;
import com.academy.orders.domain.product.repository.ProductRepository;
import com.academy.orders.domain.product.usecase.GetCountOfDiscountedProductsUseCase;
import com.academy.orders.domain.tag.repository.TagRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;

import static com.academy.orders.application.ModelUtils.getProductRequestDto;
import static com.academy.orders.application.ModelUtils.getProductRequestDtoWithDiscount;
import static com.academy.orders.application.ModelUtils.getProductRequestDtoWithInvalidLanguageCode;
import static com.academy.orders.application.ModelUtils.getProductRequestWithIncorrectUrlDto;
import static com.academy.orders.application.ModelUtils.getProductWithImageLink;
import static com.academy.orders.application.ModelUtils.getTag;
import static com.academy.orders.application.TestConstants.TEST_QUANTITY;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateProductUseCaseTest {
  @InjectMocks
  private CreateProductUseCaseImpl createProductUseCase;

  @Mock
  private GetCountOfDiscountedProductsUseCase getCountOfDiscountedProductsUseCase;

  @Mock
  private ProductRepository productRepository;

  @Mock
  private TagRepository tagRepository;

  @Mock
  private LanguageRepository languageRepository;

  @Test
  void createProductTest() {
    var request = getProductRequestDto();
    var product = getProductWithImageLink();

    when(tagRepository.getTagsByIds(request.tagIds())).thenReturn(Set.of(getTag()));
    when(languageRepository.findByCode(request.productTranslations().iterator().next().languageCode()))
        .thenReturn(Optional.ofNullable(ModelUtils.getLanguageEn()));
    when(productRepository.save(any(ProductManagement.class))).thenReturn(product);
    var result = createProductUseCase.createProduct(request);

    assertEquals(result, product);

    verify(getCountOfDiscountedProductsUseCase, never()).getCountOfDiscountedProducts();
    verify(tagRepository).getTagsByIds(request.tagIds());
    verify(languageRepository).findByCode(request.productTranslations().iterator().next().languageCode());
    verify(productRepository, times(2)).save(any(ProductManagement.class));
  }

  @Test
  void createProductNullRequestTest() {
    assertThrows(BadRequestException.class, () -> createProductUseCase.createProduct(null));
  }

  @Test
  void createProductLanguageNotFoundTest() {
    var request = getProductRequestDtoWithInvalidLanguageCode();
    var product = getProductWithImageLink();

    when(tagRepository.getTagsByIds(request.tagIds())).thenReturn(Set.of(getTag()));
    when(productRepository.save(any(ProductManagement.class))).thenReturn(product);
    when(languageRepository.findByCode("invalid")).thenThrow(new LanguageNotFoundException("invalid"));

    assertThrows(LanguageNotFoundException.class, () -> createProductUseCase.createProduct(request));

    verify(getCountOfDiscountedProductsUseCase, never()).getCountOfDiscountedProducts();
    verify(tagRepository).getTagsByIds(request.tagIds());
    verify(productRepository).save(any(ProductManagement.class));
    verify(languageRepository).findByCode("invalid");
  }

  @Test
  void createProductWhenUrlIsInvalidTest() {
    var request = getProductRequestWithIncorrectUrlDto();

    assertThrows(BadRequestException.class, () -> createProductUseCase.createProduct(request));
  }

  @Test
  void createProductWhenCountOfDiscountedProductsExceedsLimitTest() {
    var request = getProductRequestDtoWithDiscount(15);

    when(getCountOfDiscountedProductsUseCase.getCountOfDiscountedProducts()).thenReturn(10);

    assertThrows(BadRequestException.class, () -> createProductUseCase.createProduct(request));

    verify(getCountOfDiscountedProductsUseCase).getCountOfDiscountedProducts();
    verify(tagRepository, never()).getTagsByIds(any());
    verify(productRepository, never()).save(any(ProductManagement.class));
    verify(languageRepository, never()).findByCode(anyString());
  }

  @Test
  void createProductWithDiscountTest() {
    var request = getProductRequestDtoWithDiscount(TEST_QUANTITY);
    var product = getProductWithImageLink();

    when(getCountOfDiscountedProductsUseCase.getCountOfDiscountedProducts()).thenReturn(9);
    when(tagRepository.getTagsByIds(request.tagIds())).thenReturn(Set.of(getTag()));
    when(languageRepository.findByCode(request.productTranslations().iterator().next().languageCode()))
        .thenReturn(Optional.ofNullable(ModelUtils.getLanguageEn()));
    when(productRepository.save(any(ProductManagement.class)))
        .thenReturn(product);

    createProductUseCase.createProduct(request);

    verify(getCountOfDiscountedProductsUseCase).getCountOfDiscountedProducts();
    verify(tagRepository).getTagsByIds(any());
    verify(productRepository, times(2)).save(any(ProductManagement.class));
    verify(languageRepository).findByCode(anyString());
  }
}
