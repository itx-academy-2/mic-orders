package com.academy.orders.apirest.postaddresses.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static com.academy.orders.apirest.ModelUtils.getPostAddressV2;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class UserPostAddressResponseDTOMapperTest {
  private UserPostAddressResponseDTOMapper mapper;

  @BeforeEach
  public void setUp() {
    mapper = Mappers.getMapper(UserPostAddressResponseDTOMapper.class);
  }

  @Test
  void toUserPostAddressResponseDTO_Success_Test() {
    // Given
    var postAddressV2 = getPostAddressV2();

    // When
    var userPostAddressResponseDTO = mapper.toUserPostAddressResponseDTO(postAddressV2);

    // Then
    assertNotNull(userPostAddressResponseDTO);
    assertEquals(postAddressV2.id(), userPostAddressResponseDTO.getId());
    assertEquals(postAddressV2.city(), userPostAddressResponseDTO.getCity());
    assertEquals(postAddressV2.deliveryMethod().toString(), userPostAddressResponseDTO.getDeliveryMethod().getValue());
    assertEquals(postAddressV2.department(), userPostAddressResponseDTO.getDepartment());
    assertEquals(postAddressV2.recipientPhone(), userPostAddressResponseDTO.getPhone());
    assertEquals(postAddressV2.recipientFirstName(), userPostAddressResponseDTO.getFirstName());
    assertEquals(postAddressV2.recipientLastName(), userPostAddressResponseDTO.getLastName());
    assertEquals(postAddressV2.title(), userPostAddressResponseDTO.getTitle());
  }
}
