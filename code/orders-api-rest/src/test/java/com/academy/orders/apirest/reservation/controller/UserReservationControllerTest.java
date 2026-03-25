package com.academy.orders.apirest.reservation.controller;

import com.academy.orders.apirest.auth.util.SecurityUtils;
import com.academy.orders.apirest.common.ErrorHandler;
import com.academy.orders.apirest.products.mapper.ProductPreviewDTOMapper;
import com.academy.orders.apirest.reservation.mapper.UserReservationsMetadataMapper;
import com.academy.orders.domain.product.entity.Product;
import com.academy.orders.domain.reservation.entity.UserReservationsMetadata;
import com.academy.orders.domain.reservation.usecase.AddToUserReservationsUseCase;
import com.academy.orders.domain.reservation.usecase.DecreaseProductReservationQuantityUseCase;
import com.academy.orders.domain.reservation.usecase.GetUserReservationsMetadataUseCase;
import com.academy.orders.domain.reservation.usecase.GetUserReservationsUseCase;
import com.academy.orders.domain.reservation.usecase.RemoveFromUserReservationsUseCase;
import com.academy.orders_api_rest.generated.model.ProductPreviewDTO;
import com.academy.orders_api_rest.generated.model.UserReservationsMetadataDTO;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static com.academy.orders.apirest.ModelUtils.getJwtRequest;
import static com.academy.orders.apirest.TestConstants.ROLE_USER;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserReservationController.class)
@ContextConfiguration(classes = UserReservationController.class)
@Import(ErrorHandler.class)
class UserReservationControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private AddToUserReservationsUseCase addToUserReservationsUseCase;

  @MockBean
  private RemoveFromUserReservationsUseCase removeFromUserReservationsUseCase;

  @MockBean
  private DecreaseProductReservationQuantityUseCase decreaseProductReservationQuantityUseCase;

  @MockBean
  private GetUserReservationsUseCase getUserReservationsUseCase;

  @MockBean
  private GetUserReservationsMetadataUseCase getUserReservationsMetadataUseCase;

  @MockBean
  private SecurityUtils securityUtils;

  @MockBean
  private ProductPreviewDTOMapper productPreviewDTOMapper;

  @MockBean
  private UserReservationsMetadataMapper userReservationsMetadataMapper;

  private static final Long USER_ID = 1L;

  private static final String LANGUAGE_EN = "en";

  @SneakyThrows
  @Test
  void addProductToReservationsTest() {
    // Given
    UUID productId = UUID.randomUUID();
    when(securityUtils.getAuthenticatedUserId()).thenReturn(USER_ID);

    // When
    mockMvc.perform(put("/v1/my-reservations/{productId}", productId)
        .with(getJwtRequest(USER_ID, ROLE_USER)))
        .andExpect(status().isNoContent());

    // Then
    verify(securityUtils, times(1)).getAuthenticatedUserId();
    verify(addToUserReservationsUseCase, times(1)).addProductToReservations(USER_ID, productId);
  }

  @SneakyThrows
  @Test
  void removeProductFromReservationsTest() {
    // Given
    UUID productId = UUID.randomUUID();
    when(securityUtils.getAuthenticatedUserId()).thenReturn(USER_ID);

    // When
    mockMvc.perform(delete("/v1/my-reservations/{productId}", productId)
        .with(getJwtRequest(USER_ID, ROLE_USER)))
        .andExpect(status().isNoContent());

    // Then
    verify(securityUtils, times(1)).getAuthenticatedUserId();
    verify(removeFromUserReservationsUseCase, times(1)).removeProductFromReservations(USER_ID, productId);
  }

  @SneakyThrows
  @Test
  void decreaseReservationQuantityTest() {
    // Given
    UUID productId = UUID.randomUUID();
    when(securityUtils.getAuthenticatedUserId()).thenReturn(USER_ID);

    // When
    mockMvc.perform(patch("/v1/my-reservations/{productId}/decrement", productId)
        .with(getJwtRequest(USER_ID, ROLE_USER)))
        .andExpect(status().isNoContent());

    // Then
    verify(securityUtils, times(1)).getAuthenticatedUserId();
    verify(decreaseProductReservationQuantityUseCase, times(1)).decreaseReservationQuantity(USER_ID, productId);
  }

  @SneakyThrows
  @Test
  void getUserReservationsTest() {
    // Given
    List<Product> products = List.of(new Product());
    List<ProductPreviewDTO> dtoList = List.of(new ProductPreviewDTO());
    when(securityUtils.getAuthenticatedUserId()).thenReturn(USER_ID);
    when(getUserReservationsUseCase.getUserReservations(USER_ID, LANGUAGE_EN)).thenReturn(products);
    when(productPreviewDTOMapper.toProductPreviewListDTO(products)).thenReturn(dtoList);

    // When
    mockMvc.perform(get("/v1/my-reservations")
        .param("lang", LANGUAGE_EN)
        .with(getJwtRequest(USER_ID, ROLE_USER))
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());

    // Then
    verify(securityUtils, times(1)).getAuthenticatedUserId();
    verify(getUserReservationsUseCase, times(1)).getUserReservations(USER_ID, LANGUAGE_EN);
    verify(productPreviewDTOMapper, times(1)).toProductPreviewListDTO(products);
  }

  @SneakyThrows
  @Test
  void getUserReservationsMetadataTest() {
    // Given
    UserReservationsMetadata domainMetadata = new UserReservationsMetadata(BigDecimal.valueOf(5000), 3, List.of());
    UserReservationsMetadataDTO dto = new UserReservationsMetadataDTO();
    when(securityUtils.getAuthenticatedUserId()).thenReturn(USER_ID);
    when(getUserReservationsMetadataUseCase.getUserReservationsMetadata(USER_ID)).thenReturn(domainMetadata);
    when(userReservationsMetadataMapper.toDTO(domainMetadata)).thenReturn(dto);

    // When
    mockMvc.perform(get("/v1/my-reservations/metadata")
        .with(getJwtRequest(USER_ID, ROLE_USER))
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());

    // Then
    verify(securityUtils, times(1)).getAuthenticatedUserId();
    verify(getUserReservationsMetadataUseCase, times(1)).getUserReservationsMetadata(USER_ID);
    verify(userReservationsMetadataMapper, times(1)).toDTO(domainMetadata);
  }
}
