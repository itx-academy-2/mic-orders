package com.academy.orders.infrastructure.product;

import com.academy.orders.domain.product.entity.Language;
import com.academy.orders.domain.product.entity.ProductManagement;
import com.academy.orders.domain.product.entity.ProductTranslationManagement;
import com.academy.orders.infrastructure.discount.entity.DiscountEntity;
import com.academy.orders.infrastructure.language.entity.LanguageEntity;
import com.academy.orders.infrastructure.product.entity.ProductEntity;
import com.academy.orders.infrastructure.product.entity.ProductTranslationEntity;
import com.academy.orders.infrastructure.product.entity.ProductTranslationId;
import org.mapstruct.Mapper;
import org.mapstruct.Builder;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.Context;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface ProductManagementMapper {

  @Mapping(source = "discount.amount", target = "discount")
  ProductManagement fromEntity(ProductEntity productEntity);

  @Mapping(target = "productTranslations",
      expression = "java(mapProductTranslationManagement(productWithId.productTranslationManagement(), productEntity))")
  @Mapping(source = "discount", target = "discount", qualifiedByName = "fromDiscountAmount")
  ProductEntity toEntity(ProductManagement productWithId);

  @Named("fromDiscountAmount")
  default DiscountEntity fromDiscountAmount(Integer discount) {
    if (discount == null)
      return null;
    final DiscountEntity discountEntity = new DiscountEntity();
    discountEntity.setAmount(discount);
    return discountEntity;
  }

  @Named("fromProductTranslationManagement")
  default Set<ProductTranslationEntity> mapProductTranslationManagement(
      Set<ProductTranslationManagement> productTranslations, @Context ProductEntity productEntity) {
    if (productTranslations == null) {
      return Set.of();
    }

    return productTranslations.stream().map(o -> productTranslationManagement(o, productEntity))
        .collect(Collectors.toSet());
  }

  default ProductTranslationEntity productTranslationManagement(ProductTranslationManagement dto, ProductEntity productEntity) {
    if (dto == null) {
      return null;
    }
    return ProductTranslationEntity.builder()
        .productTranslationId(new ProductTranslationId(mapProductId(dto.productId()), dto.languageId())).name(dto.name())
        .description(dto.description()).product(productEntity).language(mapLanguage(dto.language())).build();
  }

  default UUID mapProductId(UUID productId) {
    if (Objects.isNull(productId)) {
      return null;
    }
    return productId;
  }

  default LanguageEntity mapLanguage(Language language) {
    if (language == null) {
      return null;
    }
    return LanguageEntity.builder().id(language.id()).code(language.code()).build();
  }

  default ProductEntity mapProduct(UUID id) {
    if (id == null) {
      return null;
    }
    return ProductEntity.builder().id(id).build();
  }
}
