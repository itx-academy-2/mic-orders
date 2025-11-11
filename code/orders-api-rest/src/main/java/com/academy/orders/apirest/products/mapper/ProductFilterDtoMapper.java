package com.academy.orders.apirest.products.mapper;

import com.academy.orders.domain.product.dto.ProductFilterDto;
import com.academy.orders_api_rest.generated.model.ProductFilterDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductFilterDtoMapper {
  ProductFilterDto fromProductFilterDTO(ProductFilterDTO dto);
}
