package com.academy.orders.application.product.usecase;

import com.academy.orders.domain.common.exception.BadRequestException;
import com.academy.orders.domain.language.exception.LanguageNotFoundException;
import com.academy.orders.domain.language.repository.LanguageRepository;
import com.academy.orders.domain.product.dto.ProductRequestDto;
import com.academy.orders.domain.product.entity.ProductManagement;
import com.academy.orders.domain.product.exception.ProductNotFoundException;
import com.academy.orders.domain.product.repository.ProductRepository;
import com.academy.orders.domain.product.usecase.GetCountOfDiscountedProductsUseCase;
import com.academy.orders.domain.tag.repository.TagRepository;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

import static com.academy.orders.application.ModelUtils.getLanguage;
import static com.academy.orders.application.ModelUtils.getProductRequestDto;
import static com.academy.orders.application.ModelUtils.getProductRequestDtoWithDiscount;
import static com.academy.orders.application.ModelUtils.getProductRequestRemoveAllTagsDto;
import static com.academy.orders.application.ModelUtils.getProductRequestWithDifferentIds;
import static com.academy.orders.application.ModelUtils.getProductRequestWithEmptyTagsDto;
import static com.academy.orders.application.ModelUtils.getProductRequestWithIncorrectUrlDto;
import static com.academy.orders.application.ModelUtils.getProductTranslationManagement;
import static com.academy.orders.application.ModelUtils.getProductWithImageLink;
import static com.academy.orders.application.ModelUtils.getProductWithImageLinkAndDiscount;
import static com.academy.orders.application.ModelUtils.getTag;
import static com.academy.orders.application.TestConstants.LANGUAGE_EN;
import static com.academy.orders.application.TestConstants.TEST_ID;
import static com.academy.orders.application.TestConstants.TEST_UUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateProductUseCaseTest {
  @InjectMocks
  private UpdateProductUseCaseImpl updateProductUseCase;

  @Mock
  private GetCountOfDiscountedProductsUseCase getCountOfDiscountedProductsUseCase;

  @Mock
  private ProductRepository productRepository;

  @Mock
  private TagRepository tagRepository;

  @Mock
  private LanguageRepository languageRepository;

  @Captor
  private ArgumentCaptor<ProductManagement> argumentCaptor;

  private static Stream<Arguments> provideProductRequestsWithTags() {
    return Stream.of(Arguments.of(getProductRequestDto(), List.of(TEST_ID)),
        Arguments.of(getProductRequestWithDifferentIds(), List.of(-1L, TEST_ID)));
  }

  private static Stream<Arguments> provideProductRequestsWithoutTags() {
    return Stream.of(Arguments.of(getProductRequestWithEmptyTagsDto()),
        Arguments.of(getProductRequestRemoveAllTagsDto()));
  }

  @ParameterizedTest
  @MethodSource("provideProductRequestsWithTags")
  void updateProductWithTagsTest(ProductRequestDto request, List<Long> tagIds) {
    var product = getProductWithImageLink();
    var productTranslationManagement = getProductTranslationManagement();
    var language = getLanguage();

    when(productRepository.getById(TEST_UUID)).thenReturn(Optional.ofNullable(product));
    when(productRepository.findTranslationsByProductId(TEST_UUID)).thenReturn(Set.of(productTranslationManagement));
    when(tagRepository.getTagsByIds(tagIds)).thenReturn(Set.of(getTag()));
    when(languageRepository.findByCode(LANGUAGE_EN)).thenReturn(Optional.ofNullable(language));

    doNothing().when(productRepository).update(any(ProductManagement.class));

    updateProductUseCase.updateProduct(TEST_UUID, request);

    verify(productRepository).getById(TEST_UUID);
    verify(productRepository).findTranslationsByProductId(TEST_UUID);
    verify(tagRepository).getTagsByIds(tagIds);
    verify(languageRepository).findByCode(LANGUAGE_EN);

    var productCaptor = ArgumentCaptor.forClass(ProductManagement.class);
    verify(productRepository).update(productCaptor.capture());
    var updatedProduct = productCaptor.getValue();

    var updatedTranslations = updatedProduct.productTranslationManagement();

    updatedTranslations.forEach(
        updatedTranslation -> assertEquals(LANGUAGE_EN, updatedTranslation.language().code()));
  }

  @ParameterizedTest
  @MethodSource("provideProductRequestsWithoutTags")
  void updateProductWithoutTagsTest(ProductRequestDto request) {
    var product = getProductWithImageLink();
    var productTranslationManagement = getProductTranslationManagement();
    var language = getLanguage();

    when(productRepository.getById(TEST_UUID)).thenReturn(Optional.ofNullable(product));
    when(productRepository.findTranslationsByProductId(TEST_UUID)).thenReturn(Set.of(productTranslationManagement));
    when(languageRepository.findByCode(LANGUAGE_EN)).thenReturn(Optional.ofNullable(language));

    doNothing().when(productRepository).update(any(ProductManagement.class));

    updateProductUseCase.updateProduct(TEST_UUID, request);

    verify(productRepository).getById(TEST_UUID);
    verify(productRepository).findTranslationsByProductId(TEST_UUID);
    verify(languageRepository).findByCode(LANGUAGE_EN);
  }

  @Test
  void updateProductThrowsProductNotFoundExceptionTest() {
    var request = getProductRequestDto();
    when(productRepository.getById(TEST_UUID)).thenReturn(Optional.empty());
    assertThrows(ProductNotFoundException.class, () -> updateProductUseCase.updateProduct(TEST_UUID, request));

    verify(productRepository).getById(TEST_UUID);
  }

  @Test
  void updateProductThrowsLanguageNotFoundExceptionTest() {
    var request = getProductRequestDto();
    var product = getProductWithImageLink();
    var productTranslationManagement = getProductTranslationManagement();

    when(productRepository.getById(TEST_UUID)).thenReturn(Optional.ofNullable(product));
    when(productRepository.findTranslationsByProductId(TEST_UUID)).thenReturn(Set.of(productTranslationManagement));
    when(tagRepository.getTagsByIds(List.of(TEST_ID))).thenReturn(Set.of(getTag()));
    when(languageRepository.findByCode(LANGUAGE_EN)).thenReturn(Optional.empty());

    assertThrows(LanguageNotFoundException.class, () -> updateProductUseCase.updateProduct(TEST_UUID, request));

    verify(productRepository).getById(TEST_UUID);
    verify(productRepository).findTranslationsByProductId(TEST_UUID);
    verify(tagRepository).getTagsByIds(List.of(TEST_ID));
  }

  @Test
  void createProductWhenUrlIsInvalidTest() {
    var request = getProductRequestWithIncorrectUrlDto();

    Assert.assertThrows(BadRequestException.class, () -> updateProductUseCase.updateProduct(TEST_UUID, request));
  }

  @Test
  void updateProductWhenCountOfDiscountsIsMaximumAndProductHasAlreadyDiscountTest() {
    var discount = 20;
    var request = getProductRequestDtoWithDiscount(discount);
    var productBeforeSettingDiscount = getProductWithImageLinkAndDiscount(10);
    var productTranslationManagement = getProductTranslationManagement();
    var language = getLanguage();

    when(productRepository.getById(TEST_UUID)).thenReturn(Optional.of(productBeforeSettingDiscount));
    when(productRepository.findTranslationsByProductId(TEST_UUID)).thenReturn(Set.of(productTranslationManagement));
    when(tagRepository.getTagsByIds(List.of(TEST_ID))).thenReturn(Set.of(getTag()));
    when(languageRepository.findByCode(LANGUAGE_EN)).thenReturn(Optional.ofNullable(language));

    updateProductUseCase.updateProduct(TEST_UUID, request);

    verify(getCountOfDiscountedProductsUseCase, never()).getCountOfDiscountedProducts();
    verify(productRepository).getById(TEST_UUID);
    verify(productRepository).findTranslationsByProductId(TEST_UUID);
    verify(tagRepository).getTagsByIds(List.of(TEST_ID));
    verify(languageRepository).findByCode(LANGUAGE_EN);
    verify(productRepository).update(argumentCaptor.capture());

    var result = argumentCaptor.getValue();
    assertEquals(discount, result.discount());
  }

  @Test
  void updateProductWhenCountOfDiscountsIsMaximumAndProductHasNoDiscountTest() {
    var request = getProductRequestDtoWithDiscount(20);
    var productBeforeSettingDiscount = getProductWithImageLink();

    when(getCountOfDiscountedProductsUseCase.getCountOfDiscountedProducts()).thenReturn(10);
    when(productRepository.getById(TEST_UUID)).thenReturn(Optional.of(productBeforeSettingDiscount));

    assertThrows(BadRequestException.class, () -> updateProductUseCase.updateProduct(TEST_UUID, request));

    verify(getCountOfDiscountedProductsUseCase).getCountOfDiscountedProducts();
    verify(productRepository).getById(any(UUID.class));
    verify(productRepository, never()).findTranslationsByProductId(TEST_UUID);
    verify(tagRepository, never()).getTagsByIds(List.of(TEST_ID));
    verify(languageRepository, never()).findByCode(LANGUAGE_EN);
    verify(productRepository, never()).update(any(ProductManagement.class));
  }

  @Test
  void updateProductWhenProductHasNoDiscountTest() {
    var discount = 20;
    var request = getProductRequestDtoWithDiscount(discount);
    var productBeforeSettingDiscount = getProductWithImageLink();
    var productTranslationManagement = getProductTranslationManagement();
    var language = getLanguage();

    when(getCountOfDiscountedProductsUseCase.getCountOfDiscountedProducts()).thenReturn(9);
    when(productRepository.getById(TEST_UUID)).thenReturn(Optional.of(productBeforeSettingDiscount));
    when(productRepository.findTranslationsByProductId(TEST_UUID)).thenReturn(Set.of(productTranslationManagement));
    when(tagRepository.getTagsByIds(List.of(TEST_ID))).thenReturn(Set.of(getTag()));
    when(languageRepository.findByCode(LANGUAGE_EN)).thenReturn(Optional.ofNullable(language));
    doNothing().when(productRepository).update(any(ProductManagement.class));

    updateProductUseCase.updateProduct(TEST_UUID, request);

    verify(getCountOfDiscountedProductsUseCase).getCountOfDiscountedProducts();
    verify(productRepository).getById(TEST_UUID);
    verify(productRepository).findTranslationsByProductId(TEST_UUID);
    verify(tagRepository).getTagsByIds(List.of(TEST_ID));
    verify(languageRepository).findByCode(LANGUAGE_EN);
    verify(productRepository).update(argumentCaptor.capture());

    var result = argumentCaptor.getValue();
    assertEquals(discount, result.discount());
  }
}
