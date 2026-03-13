package com.academy.orders.apirest.products.mapper;

import com.academy.orders.apirest.common.mapper.LocalDateTimeMapper;
import com.academy.orders.domain.common.Page;
import com.academy.orders.domain.product.dto.ProductManagementFilterDto;
import com.academy.orders.domain.product.entity.ProductManagementView;
import com.academy.orders_api_rest.generated.model.ProductManagementContentDTO;
import com.academy.orders_api_rest.generated.model.ProductManagementFilterDTO;
import com.academy.orders_api_rest.generated.model.ProductManagementPageDTO;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR, uses = LocalDateTimeMapper.class)
public interface ManagementProductMapper extends ProductMapper {
  ProductManagementFilterDto fromProductManagementFilterDTO(ProductManagementFilterDTO productManagementFilter);

  @Mapping(target = "id", source = "product.id")
  @Mapping(target = "quantity", source = "product.quantity")
  @Mapping(target = "price", source = "product.price")
  @Mapping(target = "createdAt", source = "product.createdAt")
  @Mapping(target = "status", source = "product.status")
  @Mapping(target = "percentageOfTotalOrders", source = "product.percentageOfTotalOrders")
  @Mapping(target = "imageLink", source = "product.image")
  @Mapping(target = "name", source = "product.productTranslations", qualifiedByName = "mapProductName")
  @Mapping(target = "tags", source = "product.tags", qualifiedByName = "mapTags")
  @Mapping(target = "discount", source = "product.discount.amount")
  @Mapping(target = "priceWithDiscount", expression = "java(view.getProduct().getPriceWithDiscount())")
  @Mapping(target = "reservedQuantity", source = "reservedQuantity")
  ProductManagementContentDTO fromProductManagementView(ProductManagementView view);

  ProductManagementPageDTO fromProductManagementViewPage(Page<ProductManagementView> productPage);
}
