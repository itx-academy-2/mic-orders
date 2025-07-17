package com.academy.orders.apirest.accountsV2.controller;

import com.academy.orders.apirest.accountsV2.mapper.AccountV2DTOMapper;
import com.academy.orders.apirest.accountsV2.mapper.AccountV2InfoUpdateMapper;
import com.academy.orders.apirest.auth.util.SecurityUtils;
import com.academy.orders.domain.accountv2.usecase.GetUserAccountV2InfoUseCase;
import com.academy.orders.domain.accountv2.usecase.UpdateUserAccountV2InfoUseCase;
import com.academy.orders_api_rest.generated.api.UserPersonalCabinetApi;
import com.academy.orders_api_rest.generated.model.UpdateAccountV2InfoRequestDTO;
import com.academy.orders_api_rest.generated.model.UserAccountInfoDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserCabinetController implements UserPersonalCabinetApi {
  private final GetUserAccountV2InfoUseCase getUserAccountV2InfoUseCase;

  private final UpdateUserAccountV2InfoUseCase updateUserAccountV2InfoUseCase;

  private final SecurityUtils securityUtils;

  private final AccountV2DTOMapper accountV2DTOMapper;

  private final AccountV2InfoUpdateMapper accountV2InfoUpdateMapper;

  @Override
  public ResponseEntity<UserAccountInfoDTO> getPersonalUserInfo() {
    Long userId = securityUtils.getAuthenticatedUserId();
    var accountV2 = getUserAccountV2InfoUseCase.getUserAccountInfo(userId);
    return ResponseEntity.ok(accountV2DTOMapper.toUserAccountInfoDto(accountV2));
  }

  @Override
  public ResponseEntity<Void> updatePersonalUserInfo(UpdateAccountV2InfoRequestDTO updateAccountV2InfoRequestDTO) {
    Long userId = securityUtils.getAuthenticatedUserId();
    var updateDto = accountV2InfoUpdateMapper.toUpdateUserAccountV2InfoDto(updateAccountV2InfoRequestDTO);
    updateUserAccountV2InfoUseCase.updateUserAccountInfo(userId, updateDto);
    return ResponseEntity.noContent().build();
  }
}
