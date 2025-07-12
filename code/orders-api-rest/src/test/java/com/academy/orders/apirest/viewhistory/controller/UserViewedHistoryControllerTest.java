package com.academy.orders.apirest.viewhistory.controller;

import com.academy.orders.apirest.auth.util.SecurityUtils;
import com.academy.orders.apirest.common.ErrorHandler;
import com.academy.orders.apirest.common.mapper.PageableDTOMapper;
import com.academy.orders.apirest.products.mapper.ProductPreviewDTOMapper;
import com.academy.orders.domain.viewhistory.usecase.AddProductToViewedHistoryUseCase;
import com.academy.orders.domain.viewhistory.usecase.DeleteAllViewedProductsUseCase;
import com.academy.orders.domain.viewhistory.usecase.DeleteProductFromViewedHistoryUseCase;
import com.academy.orders.domain.viewhistory.usecase.GetViewedProductsUseCase;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import java.util.UUID;

import static com.academy.orders.apirest.ModelUtils.getJwtRequest;
import static com.academy.orders.apirest.ModelUtils.getPageProductsDTO;
import static com.academy.orders.apirest.ModelUtils.getPageable;
import static com.academy.orders.apirest.ModelUtils.getPageableDTO;
import static com.academy.orders.apirest.ModelUtils.getProductsPage;
import static com.academy.orders.apirest.TestConstants.LANGUAGE_EN;
import static com.academy.orders.apirest.TestConstants.ROLE_USER;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserViewedHistoryController.class)
@ContextConfiguration(classes = UserViewedHistoryController.class)
@Import(ErrorHandler.class)
class UserViewedHistoryControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private AddProductToViewedHistoryUseCase addProductToViewedHistoryUseCase;

  @MockBean
  private DeleteAllViewedProductsUseCase deleteAllViewedProductsUseCase;

  @MockBean
  private DeleteProductFromViewedHistoryUseCase deleteProductFromViewedHistoryUseCase;

  @MockBean
  private GetViewedProductsUseCase getViewedProductsUseCase;

  @MockBean
  private SecurityUtils securityUtils;

  @MockBean
  private PageableDTOMapper pageableDTOMapper;

  @MockBean
  private ProductPreviewDTOMapper productPreviewDTOMapper;

  private static final Long USER_ID = 1L;

  @SneakyThrows
  @Test
  void addProductToViewedHistoryTest() {
    // Given
    UUID productId = UUID.randomUUID();
    when(securityUtils.getAuthenticatedUserId()).thenReturn(USER_ID);

    // When
    mockMvc.perform(put("/v1/my-view-history/{productId}", productId)
        .with(getJwtRequest(USER_ID, ROLE_USER)))
        .andExpect(status().isOk());

    // Then
    verify(securityUtils, times(1)).getAuthenticatedUserId();
    verify(addProductToViewedHistoryUseCase, times(1)).addProductToViewedHistory(USER_ID, productId);
  }

  @SneakyThrows
  @Test
  void deleteAllViewedProductsTest() {
    // Given
    when(securityUtils.getAuthenticatedUserId()).thenReturn(USER_ID);

    // When
    mockMvc.perform(delete("/v1/my-view-history")
        .with(getJwtRequest(USER_ID, ROLE_USER)))
        .andExpect(status().isNoContent());

    // Then
    verify(securityUtils, times(1)).getAuthenticatedUserId();
    verify(deleteAllViewedProductsUseCase, times(1)).deleteAllViewedProducts(USER_ID);
  }

  @SneakyThrows
  @Test
  void deleteViewedProductTest() {
    // Given
    UUID productId = UUID.randomUUID();
    when(securityUtils.getAuthenticatedUserId()).thenReturn(USER_ID);

    // When
    mockMvc.perform(delete("/v1/my-view-history/{productId}", productId)
        .with(getJwtRequest(USER_ID, ROLE_USER)))
        .andExpect(status().isNoContent());

    // Then
    verify(securityUtils, times(1)).getAuthenticatedUserId();
    verify(deleteProductFromViewedHistoryUseCase, times(1)).deleteProductFromViewedHistory(USER_ID, productId);
  }

  @SneakyThrows
  @Test
  void getViewedProductsTest() {
    // Given
    var pageableDTO = getPageableDTO();
    var pageable = getPageable();
    var pageProducts = getProductsPage();
    var responseDto = getPageProductsDTO();
    when(securityUtils.getAuthenticatedUserId()).thenReturn(USER_ID);
    when(pageableDTOMapper.fromDto(pageableDTO)).thenReturn(pageable);
    when(getViewedProductsUseCase.getViewedProducts(USER_ID, pageable, LANGUAGE_EN)).thenReturn(pageProducts);
    when(productPreviewDTOMapper.toPageProductsDTO(pageProducts)).thenReturn(responseDto);

    // When
    mockMvc.perform(get("/v1/my-view-history")
        .param("lang", LANGUAGE_EN)
        .with(getJwtRequest(USER_ID, ROLE_USER)))
        .andExpect(status().isOk());

    // Then
    verify(securityUtils, times(1)).getAuthenticatedUserId();
    verify(pageableDTOMapper, times(1)).fromDto(pageableDTO);
    verify(getViewedProductsUseCase, times(1)).getViewedProducts(USER_ID, pageable, "en");
    verify(productPreviewDTOMapper, times(1)).toPageProductsDTO(pageProducts);
  }
}
