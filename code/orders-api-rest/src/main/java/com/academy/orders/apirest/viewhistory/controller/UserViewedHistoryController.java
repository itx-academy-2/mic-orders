package com.academy.orders.apirest.viewhistory.controller;

import com.academy.orders.apirest.auth.util.SecurityUtils;
import com.academy.orders.apirest.common.mapper.PageableDTOMapper;
import com.academy.orders.apirest.products.mapper.ProductPreviewDTOMapper;
import com.academy.orders.domain.common.Page;
import com.academy.orders.domain.product.entity.Product;
import com.academy.orders.domain.viewhistory.usecase.AddProductToViewedHistoryUseCase;
import com.academy.orders.domain.viewhistory.usecase.DeleteAllViewedProductsUseCase;
import com.academy.orders.domain.viewhistory.usecase.DeleteProductFromViewedHistoryUseCase;
import com.academy.orders.domain.viewhistory.usecase.GetViewedProductsUseCase;
import com.academy.orders_api_rest.generated.api.ViewHistoryApi;
import com.academy.orders_api_rest.generated.model.PageProductsDTO;
import com.academy.orders_api_rest.generated.model.PageableDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserViewedHistoryController implements ViewHistoryApi {

  private final AddProductToViewedHistoryUseCase addProductToViewedHistoryUseCase;

  private final DeleteAllViewedProductsUseCase deleteAllViewedProductsUseCase;

  private final DeleteProductFromViewedHistoryUseCase deleteProductFromViewedHistoryUseCase;

  private final GetViewedProductsUseCase getViewedProductsUseCase;

  private final SecurityUtils securityUtils;

  private final PageableDTOMapper pageableDTOMapper;

  private final ProductPreviewDTOMapper productPreviewDTOMapper;

  @Override
  public ResponseEntity<Void> addProductToViewedHistory(UUID productId) {
    Long userId = securityUtils.getAuthenticatedUserId();
    log.info("User {} is adding product {} to viewed history", userId, productId);
    addProductToViewedHistoryUseCase.addProductToViewedHistory(userId, productId);
    return ResponseEntity.status(200).build();
  }

  @Override
  public ResponseEntity<Void> deleteAllViewedProducts() {
    Long userId = securityUtils.getAuthenticatedUserId();
    log.info("User {} is deleting all products from viewed history", userId);
    deleteAllViewedProductsUseCase.deleteAllViewedProducts(userId);
    return ResponseEntity.noContent().build();
  }

  @Override
  public ResponseEntity<Void> deleteViewedProduct(UUID productId) {
    Long userId = securityUtils.getAuthenticatedUserId();
    log.info("User {} is deleting product {} from viewed history", userId, productId);
    deleteProductFromViewedHistoryUseCase.deleteProductFromViewedHistory(userId, productId);
    return ResponseEntity.noContent().build();
  }

  @Override
  public ResponseEntity<PageProductsDTO> getViewedProducts(PageableDTO pageableDTO, String lang) {
    Long userId = securityUtils.getAuthenticatedUserId();
    var pageable = pageableDTOMapper.fromDto(pageableDTO);
    log.info("User {} is fetching viewed products with language '{}' and pageable: {}", userId, lang, pageable);
    Page<Product> products = getViewedProductsUseCase.getViewedProducts(userId, pageable, lang);
    return ResponseEntity.ok(productPreviewDTOMapper.toPageProductsDTO(products));
  }
}
