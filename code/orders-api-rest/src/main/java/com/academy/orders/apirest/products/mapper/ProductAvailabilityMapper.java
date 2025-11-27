package com.academy.orders.apirest.products.mapper;

import com.academy.orders.domain.product.entity.Product;
import com.academy.orders.domain.product.entity.enumerated.ProductAvailability;
import com.academy.orders_api_rest.generated.model.ProductAvailabilityStatusDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductAvailabilityMapper {

  default ProductAvailabilityStatusDTO map(Product product) {
    ProductAvailability availability = product.getAvailability();
    return switch (availability) {
      case AVAILABLE -> ProductAvailabilityStatusDTO.AVAILABLE;
      case END_SOON -> ProductAvailabilityStatusDTO.END_SOON;
      case ENDED -> ProductAvailabilityStatusDTO.ENDED;
    };
  }
}
