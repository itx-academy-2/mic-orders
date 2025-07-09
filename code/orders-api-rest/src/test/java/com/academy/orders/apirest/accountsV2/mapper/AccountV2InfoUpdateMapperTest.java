package com.academy.orders.apirest.accountsV2.mapper;

import com.academy.orders.domain.accountv2.dto.UpdateUserAccountV2InfoDto;
import com.academy.orders_api_rest.generated.model.UpdateAccountV2InfoRequestDTO;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static com.academy.orders.apirest.ModelUtils.getUpdateAccountV2InfoRequestDTO;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AccountV2InfoUpdateMapperTest {

  private final AccountV2InfoUpdateMapper mapper = Mappers.getMapper(AccountV2InfoUpdateMapper.class);

  @Test
  void toUpdateUserAccountV2InfoDtoTest() {
    // Given
    UpdateAccountV2InfoRequestDTO dto = getUpdateAccountV2InfoRequestDTO();
    UpdateUserAccountV2InfoDto expected = UpdateUserAccountV2InfoDto.builder()
        .firstName(dto.getFirstName())
        .lastName(dto.getLastName())
        .phone(dto.getPhone())
        .build();

    // When
    UpdateUserAccountV2InfoDto result = mapper.toUpdateUserAccountV2InfoDto(dto);

    // Then
    assertEquals(expected, result);
  }
}
