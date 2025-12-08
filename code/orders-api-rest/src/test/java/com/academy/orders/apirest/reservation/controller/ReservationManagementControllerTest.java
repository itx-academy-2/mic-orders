package com.academy.orders.apirest.reservation.controller;

import com.academy.orders.apirest.common.mapper.PageableDTOMapper;
import com.academy.orders.apirest.reservation.mapper.ProductReservationDetailsDTOMapper;
import com.academy.orders.domain.common.Page;
import com.academy.orders.domain.common.Pageable;
import com.academy.orders.domain.reservation.entity.ProductReservationDetails;
import com.academy.orders.domain.reservation.usecase.GetProductReservationsInfoUseCase;
import com.academy.orders_api_rest.generated.model.PageProductReservationDetailsDTO;
import com.academy.orders_api_rest.generated.model.PageableDTO;
import com.academy.orders_api_rest.generated.model.ProductReservationDetailsDTO;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;
import java.util.UUID;

import static com.academy.orders.apirest.ModelUtils.*;
import static com.academy.orders.apirest.TestConstants.ROLE_MANAGER;
import static com.academy.orders.apirest.TestConstants.TEST_ID;
import static com.academy.orders.apirest.TestConstants.TEST_UUID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ReservationManagementController.class)
@ContextConfiguration(classes = ReservationManagementController.class)
class ReservationManagementControllerTest {

  private static final UUID PRODUCT_ID = TEST_UUID;

  private static final String GET_RESERVATIONS_PATH = "/v1/management/products/{productId}/reservations";

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private GetProductReservationsInfoUseCase getProductReservationsInfoUseCase;

  @MockBean
  private PageableDTOMapper pageableDTOMapper;

  @MockBean
  private ProductReservationDetailsDTOMapper productReservationDetailsDTOMapper;

  @SneakyThrows
  @Test
  void getProductReservationsInfoTest() {
    // Given
    ProductReservationDetails productReservationDetails = createProductReservationDetails();
    ProductReservationDetailsDTO pageProductReservationDetailsDTO = createProductReservationDetailsDTO();
    Page<ProductReservationDetails> page = getProductReservationDetailsPage(List.of(productReservationDetails), 0, 10);
    PageProductReservationDetailsDTO responseDto = getProductReservationDetailsDtoPage(List.of(pageProductReservationDetailsDTO), 0, 10);
    Pageable pageable = getPageable();
    when(pageableDTOMapper.fromDto(any(PageableDTO.class))).thenReturn(pageable);
    when(getProductReservationsInfoUseCase.getProductReservationsInfo(PRODUCT_ID, pageable)).thenReturn(page);
    when(productReservationDetailsDTOMapper.toPageProductReservationDetailsDTO(page)).thenReturn(responseDto);

    // When
    mockMvc.perform(get(GET_RESERVATIONS_PATH, PRODUCT_ID)
        .param("page", "0")
        .param("size", "10")
        .with(getJwtRequest(TEST_ID, ROLE_MANAGER)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].email").value("john.doe@example.com"))
        .andExpect(jsonPath("$.content[0].addedAt").value(OFFSET_DATE_TIME.toString()));

    // Then
    verify(pageableDTOMapper).fromDto(any(PageableDTO.class));
    verify(getProductReservationsInfoUseCase).getProductReservationsInfo(PRODUCT_ID, pageable);
    verify(productReservationDetailsDTOMapper).toPageProductReservationDetailsDTO(page);
  }
}
