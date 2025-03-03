package com.academy.orders.apirest.products.mapper;

import com.academy.orders.domain.product.dto.ProductsOnSaleResponseDto;
import com.academy.orders_api_rest.generated.model.ProductsOnSaleResponseDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = ProductPreviewDTOMapper.class)
public interface ProductsOnSaleResponseDTOMapper {
  ProductsOnSaleResponseDTO toProductsOnSaleResponseDTO(ProductsOnSaleResponseDto productsOnSaleResponseDto);
}
