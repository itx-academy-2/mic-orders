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
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ProductManagementMapper {

  @Mapping(source = "discount.amount", target = "discount")
  ProductManagement fromEntity(ProductEntity productEntity);

  @Mapping(source = "productTranslationManagement", target = "productTranslations")
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

  default Set<ProductTranslationEntity> mapProductTranslationManagement(
      Set<ProductTranslationManagement> productTranslationManagement) {
    if (productTranslationManagement == null) {
      return Set.of();
    }
    return productTranslationManagement.stream().map(this::productTranslationManagement)
        .collect(Collectors.toSet());
  }

  default ProductTranslationEntity productTranslationManagement(ProductTranslationManagement dto) {
    if (dto == null) {
      return null;
    }
    return ProductTranslationEntity.builder().product(mapProduct(dto.productId()))
        .productTranslationId(new ProductTranslationId(dto.productId(), dto.languageId())).name(dto.name())
        .description(dto.description()).language(mapLanguage(dto.language())).build();
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
