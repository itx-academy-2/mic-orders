package com.academy.orders.apirest.wishlist.controller;

import com.academy.orders.apirest.auth.util.SecurityUtils;
import com.academy.orders.apirest.common.mapper.PageableDTOMapper;
import com.academy.orders.apirest.products.mapper.ProductPreviewDTOMapper;
import com.academy.orders.domain.common.Page;
import com.academy.orders.domain.common.Pageable;
import com.academy.orders.domain.product.entity.Product;
import com.academy.orders.domain.wishlist.usecase.AddToWishlistUseCase;
import com.academy.orders.domain.wishlist.usecase.GetUserWishlistUseCase;
import com.academy.orders.domain.wishlist.usecase.RemoveFromWishlistUseCase;
import com.academy.orders_api_rest.generated.model.PageProductsDTO;
import com.academy.orders_api_rest.generated.model.PageableDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
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
import static com.academy.orders.apirest.TestConstants.TEST_ID;
import static com.academy.orders.apirest.TestConstants.TEST_UUID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserWishlistController.class)
@ContextConfiguration(classes = UserWishlistController.class)
class UserWishlistControllerTest {

  private static final Long USER_ID = TEST_ID;

  private static final UUID PRODUCT_ID = TEST_UUID;

  private static final String MY_WISHLIST_PATH = "/v1/my-wishlist";

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private AddToWishlistUseCase addToWishlistUseCase;

  @MockBean
  private RemoveFromWishlistUseCase removeFromWishlistUseCase;

  @MockBean
  private GetUserWishlistUseCase getUserWishlistUseCase;

  @MockBean
  private SecurityUtils securityUtils;

  @MockBean
  private PageableDTOMapper pageableDTOMapper;

  @MockBean
  private ProductPreviewDTOMapper productPreviewDTOMapper;

  @SneakyThrows
  @Test
  void addToWishlistTest() {
    // Given
    when(securityUtils.getAuthenticatedUserId()).thenReturn(USER_ID);

    // When
    mockMvc.perform(put(MY_WISHLIST_PATH + "/{productId}", PRODUCT_ID)
        .with(getJwtRequest(USER_ID, ROLE_USER)))
        .andExpect(status().isNoContent());

    // Then
    verify(securityUtils, times(1)).getAuthenticatedUserId();
    verify(addToWishlistUseCase, times(1)).addProductToWishlist(USER_ID, PRODUCT_ID);
  }

  @SneakyThrows
  @Test
  void removeFromWishlistTest() {
    // Given
    when(securityUtils.getAuthenticatedUserId()).thenReturn(USER_ID);

    // When
    mockMvc.perform(delete(MY_WISHLIST_PATH + "/{productId}", PRODUCT_ID)
        .with(getJwtRequest(USER_ID, ROLE_USER)))
        .andExpect(status().isNoContent());

    // Then
    verify(securityUtils, times(1)).getAuthenticatedUserId();
    verify(removeFromWishlistUseCase, times(1)).removeProductFromWishlist(USER_ID, PRODUCT_ID);
  }

  @SneakyThrows
  @Test
  void getUserWishlistTest() {
    // Given
    PageableDTO dto = getPageableDTO();
    Pageable pageable = getPageable();
    Page<Product> page = getProductsPage();
    PageProductsDTO responseDto = getPageProductsDTO();
    when(securityUtils.getAuthenticatedUserId()).thenReturn(USER_ID);
    when(pageableDTOMapper.fromDto(any(PageableDTO.class))).thenReturn(pageable);
    when(getUserWishlistUseCase.getProductsInUserWishlist(USER_ID, LANGUAGE_EN, pageable)).thenReturn(page);
    when(productPreviewDTOMapper.toPageProductsDTO(page)).thenReturn(responseDto);

    // When
    mockMvc.perform(get(MY_WISHLIST_PATH)
        .param("lang", LANGUAGE_EN)
        .param("page", "0")
        .param("size", "10")
        .with(getJwtRequest(USER_ID, ROLE_USER)))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().json(new ObjectMapper().writeValueAsString(responseDto)));

    // Then
    verify(securityUtils).getAuthenticatedUserId();
    verify(pageableDTOMapper).fromDto(any(PageableDTO.class));
    verify(getUserWishlistUseCase).getProductsInUserWishlist(USER_ID, LANGUAGE_EN, pageable);
    verify(productPreviewDTOMapper).toPageProductsDTO(page);
  }
}
