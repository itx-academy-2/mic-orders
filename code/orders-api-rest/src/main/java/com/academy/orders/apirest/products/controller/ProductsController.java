package com.academy.orders.apirest.products.controller;

import com.academy.orders.apirest.common.mapper.PageableDTOMapper;
import com.academy.orders.apirest.products.mapper.PageProductSearchResultDTOMapper;
import com.academy.orders.apirest.products.mapper.ProductDetailsResponseDTOMapper;
import com.academy.orders.apirest.products.mapper.ProductPreviewDTOMapper;
import com.academy.orders.apirest.products.mapper.ProductsOnSaleFilterMapper;
import com.academy.orders.apirest.products.mapper.ProductsOnSaleResponseDTOMapper;
import com.academy.orders.domain.common.Page;
import com.academy.orders.domain.common.Pageable;
import com.academy.orders.domain.product.dto.ProductsOnSaleFilterDto;
import com.academy.orders.domain.product.entity.Product;
import com.academy.orders.domain.product.usecase.*;
import com.academy.orders_api_rest.generated.api.ProductsApi;
import com.academy.orders_api_rest.generated.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
@CrossOrigin
public class ProductsController implements ProductsApi {
  private final GetAllProductsUseCase getAllProductsUseCase;

  private final GetProductsOnSaleUseCase getProductsOnSaleUseCase;

  private final GetProductDetailsByIdUseCase getProductDetailsByIdUseCase;

  private final GetProductSearchResultsUseCase getProductSearchResultsUseCase;

  private final ProductPreviewDTOMapper productPreviewDTOMapper;

  private final PageableDTOMapper pageableDTOMapper;

  private final PageProductSearchResultDTOMapper pageProductSearchResultDTOMapper;

  private final ProductsOnSaleFilterMapper productsOnSaleFilterMapper;

  private final ProductsOnSaleResponseDTOMapper productsOnSaleResponseDTOMapper;

  private final ProductDetailsResponseDTOMapper productDetailsResponseDTOMapper;

  private final FindProductsBestsellersUseCase findProductsBestsellersUseCase;

  private final FindMostSoldProductByTagUseCase findMostSoldProductByTagUseCase;

  @Override
  public ProductDetailsResponseDTO getProductDetailsById(UUID productId, String lang) {
    var product = getProductDetailsByIdUseCase.getProductDetailsById(productId, lang);
    return productDetailsResponseDTOMapper.toDTO(product);
  }

  @Override
  public ProductsOnSaleResponseDTO getProductsOnSale(ProductsOnSaleFilterDTO filter, PageableDTO pageableDTO, String lang) {
    final ProductsOnSaleFilterDto productsOnSaleFilterDto = productsOnSaleFilterMapper.fromDto(filter);
    var pageable = pageableDTOMapper.fromDto(pageableDTO);
    var productsOnSale = getProductsOnSaleUseCase.getProductsOnSale(productsOnSaleFilterDto, pageable, lang);
    return productsOnSaleResponseDTOMapper.toProductsOnSaleResponseDTO(productsOnSale);
  }

  @Override
  public PageProductsDTO getProducts(ProductFilterDTO productFilter, PageableDTO dto, String lang) {
    log.debug("Get all products by language code: {}", lang);
    var pageable = pageableDTOMapper.fromDto(dto);
    var products = getAllProductsUseCase.getAllProducts(lang, pageable, productFilter.getTags());
    return productPreviewDTOMapper.toPageProductsDTO(products);
  }

  @Override
  public PageProductSearchResultDTO searchProducts(String searchQuery, String lang, PageableDTO pageable) {
    Pageable pageableDomain = pageableDTOMapper.fromDto(pageable);
    var products = getProductSearchResultsUseCase.findProductsBySearchQuery(searchQuery, lang, pageableDomain);
    return pageProductSearchResultDTOMapper.toDto(products);
  }

  @Override
  public PageProductsDTO findBestsellers(Integer page, Integer size, String lang) {
    final Pageable pageable = Pageable.builder()
        .size(size)
        .page(page)
        .build();
    final Page<Product> products = findProductsBestsellersUseCase.findProductsBestsellers(pageable, lang);
    return productPreviewDTOMapper.toPageProductsDTO(products);
  }

  @Override
  public ProductPreviewDTO findMostSoldProductByTag(final String tag, final String lang) {
    return productPreviewDTOMapper.toDto(findMostSoldProductByTagUseCase.findMostSoldProductByTag(lang, tag));
  }
}
