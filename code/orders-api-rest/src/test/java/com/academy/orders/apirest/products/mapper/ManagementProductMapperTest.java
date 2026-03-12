package com.academy.orders.apirest.products.mapper;

import com.academy.orders.apirest.ModelUtils;
import com.academy.orders.apirest.common.mapper.LocalDateTimeMapperImpl;
import com.academy.orders.domain.product.dto.ProductManagementFilterDto;
import com.academy.orders.domain.product.entity.Tag;
import com.academy.orders.domain.product.entity.enumerated.ProductStatus;
import com.academy.orders_api_rest.generated.model.ProductManagementContentDTO;
import com.academy.orders_api_rest.generated.model.ProductManagementPageDTO;
import com.academy.orders_api_rest.generated.model.ProductManagementStatusDTO;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static com.academy.orders.apirest.ModelUtils.getProduct;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ManagementProductMapperTest {

  private final ManagementProductMapper managementProductMapper = new ManagementProductMapperImpl(new LocalDateTimeMapperImpl());

  @Test
  void fromProductManagementFilterDTOTest() {
    // Given
    var productManagementFilter = ModelUtils.getProductManagementFilterDTO();
    var expected = ProductManagementFilterDto.builder()
        .status(ProductStatus.valueOf(productManagementFilter.getStatus().name()))
        .searchByName(productManagementFilter.getSearchByName())
        .priceMore(BigDecimal.valueOf(productManagementFilter.getPriceMore()))
        .priceLess(BigDecimal.valueOf(productManagementFilter.getPriceLess()))
        .quantityMore(productManagementFilter.getQuantityMore().intValue())
        .quantityLess(productManagementFilter.getQuantityLess().intValue())
        .createdAfter(productManagementFilter.getCreatedAfter().toLocalDateTime())
        .createdBefore(productManagementFilter.getCreatedBefore().toLocalDateTime())
        .tags(productManagementFilter.getTags())
        .build();

    // When
    var result = managementProductMapper.fromProductManagementFilterDTO(productManagementFilter);

    // Then
    assertEquals(expected, result);
  }

  @Test
  void fromProductManagementViewTest() {
    // Given
    var product = getProduct();
    var productManagementView = ModelUtils.getProductManagementView(product, 2);
    var expected = new ProductManagementContentDTO()
        .id(productManagementView.getProduct().getId())
        .name(productManagementView.getProduct().getProductTranslations().iterator().next().name())
        .imageLink(productManagementView.getProduct().getImage())
        .quantity(BigDecimal.valueOf(productManagementView.getProduct().getQuantity()))
        .reservedQuantity(BigDecimal.valueOf(productManagementView.getReservedQuantity()))
        .price(productManagementView.getProduct().getPrice())
        .createdAt(OffsetDateTime.of(productManagementView.getProduct().getCreatedAt(), ZoneOffset.UTC))
        .status(ProductManagementStatusDTO.fromValue(productManagementView.getProduct().getStatus().toString()))
        .tags(productManagementView.getProduct().getTags().stream().map(Tag::name).toList())
        .discount(productManagementView.getProduct().getDiscountAmount())
        .percentageOfTotalOrders(productManagementView.getProduct().getPercentageOfTotalOrders());

    // When
    var result = managementProductMapper.fromProductManagementView(productManagementView);

    // Then
    assertEquals(expected, result);
  }

  @Test
  void fromProductManagementViewPageTest() {
    // Given
    var product = getProduct();
    var productManagementView = ModelUtils.getProductManagementView(product, 2);
    var productPage = ModelUtils.getPageOf(productManagementView);
    var productManagementContentDTO = ModelUtils.getProductManagementContentDTO(productManagementView);
    var expected = new ProductManagementPageDTO()
        .totalElements(productPage.totalElements())
        .totalPages(productPage.totalPages())
        .first(productPage.first())
        .last(productPage.last())
        .number(productPage.number())
        .numberOfElements(productPage.numberOfElements())
        .size(productPage.size())
        .empty(productPage.empty())
        .content(List.of(productManagementContentDTO));

    // When
    var result = managementProductMapper.fromProductManagementViewPage(productPage);

    // Then
    assertEquals(expected, result);
  }
}
