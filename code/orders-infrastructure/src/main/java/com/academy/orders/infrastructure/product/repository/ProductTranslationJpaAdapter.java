package com.academy.orders.infrastructure.product.repository;

import com.academy.orders.infrastructure.product.entity.ProductTranslationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductTranslationJpaAdapter
    extends JpaRepository<ProductTranslationEntity, Long>, JpaSpecificationExecutor<ProductTranslationEntity> {

}
