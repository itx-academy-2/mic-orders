package com.academy.orders.apirest.accountsV2.mapper;

import com.academy.orders.apirest.ModelUtils;
import com.academy.orders.domain.accountv2.entity.AccountV2;
import com.academy.orders_api_rest.generated.model.UserAccountInfoDTO;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AccountV2DTOMapperTest {
  private final AccountV2DTOMapper accountDTOMapper = Mappers.getMapper(AccountV2DTOMapper.class);

  @Test
  void toUserAccountInfoDtoFromAccount() {
    // Given
    AccountV2 accountV2 = ModelUtils.getAccountV2();
    UserAccountInfoDTO expected = new UserAccountInfoDTO();
    expected.setFirstName(accountV2.firstName());
    expected.setLastName(accountV2.lastName());
    expected.setEmail(accountV2.email());
    expected.setCreatedAt(accountV2.createdAt().atOffset(ZoneOffset.UTC));
    expected.setPhone(accountV2.phone());
    expected.setPhoto(accountV2.photo());

    // When
    UserAccountInfoDTO result = accountDTOMapper.toUserAccountInfoDto(accountV2);

    // Then
    assertEquals(expected, result);
  }
}
