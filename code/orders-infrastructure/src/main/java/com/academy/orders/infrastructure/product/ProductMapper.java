package com.academy.orders.infrastructure.product;

import com.academy.orders.domain.product.entity.Product;
import com.academy.orders.infrastructure.product.entity.ProductEntity;
import com.academy.orders.infrastructure.product.entity.ProductTranslationEntity;
import com.academy.orders.infrastructure.tag.TagMapper;
import com.academy.orders.infrastructure.tag.entity.TagEntity;
import org.hibernate.Hibernate;
import org.mapstruct.Condition;
import org.mapstruct.Mapper;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring", uses = {TagMapper.class, ProductTranslationMapper.class})
public interface ProductMapper {
  Product fromEntity(ProductEntity productEntity);

  default Product fromEntity(ProductTranslationEntity translationEntity) {
    ProductEntity product = translationEntity.getProduct();
    product.setProductTranslations(Set.of(translationEntity));
    return fromEntity(product);
  }

  List<Product> fromEntities(List<ProductEntity> productEntities);

  ProductEntity toEntity(Product product);

  @Condition
  default boolean isNotLazyLoadedTagEntity(Collection<TagEntity> source) {
    return Hibernate.isInitialized(source);
  }
}
