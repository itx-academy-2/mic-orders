package com.academy.orders.apirest.products.mapper;

import com.academy.orders.domain.product.dto.ProductsOnSaleFilterDto;
import com.academy.orders_api_rest.generated.model.ProductsOnSaleFilterDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductsOnSaleFilterMapper {
  ProductsOnSaleFilterDto fromDto(ProductsOnSaleFilterDTO dto);
}
