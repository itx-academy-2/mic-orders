package com.academy.orders.apirest.passwordreset.mapper;

import com.academy.orders.domain.passwordreset.dto.PasswordResetCommand;
import com.academy.orders.domain.passwordreset.dto.TokenValidationResult;
import com.academy.orders.domain.passwordreset.dto.TokenWrapper;
import com.academy.orders_api_rest.generated.model.PasswordResetRequestDTO;
import com.academy.orders_api_rest.generated.model.PasswordResetSuccessResponseDTO;
import com.academy.orders_api_rest.generated.model.TokenValidResponseDTO;

import java.util.UUID;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PasswordResetMapper {

  PasswordResetCommand toCommand(PasswordResetRequestDTO dto);

  @Mapping(target = "message", constant = "Password has been successfully reset")
  @Mapping(target = "token", source = "token")
  @Mapping(target = "timestamp", expression = "java(java.time.OffsetDateTime.now(java.time.ZoneId.of(\"UTC\")))")
  PasswordResetSuccessResponseDTO toSuccessResponseForReset(TokenWrapper wrapper);

  @Mapping(target = "valid", source = "valid")
  @Mapping(target = "token", source = "token")
  @Mapping(target = "newToken", source = "newToken")
  @Mapping(target = "tokenType", source = "tokenType")
  @Mapping(target = "expiresAt", expression = "java(result.expiresAt() == null ? null : java.time.OffsetDateTime.ofInstant(result.expiresAt(), java.time.ZoneId.of(\"UTC\")))")
  @Mapping(target = "timestamp", expression = "java(result.timestamp() == null ? null : java.time.OffsetDateTime.ofInstant(result.timestamp(), java.time.ZoneId.of(\"UTC\")))")
  TokenValidResponseDTO toTokenValidResponse(TokenValidationResult result);

  default String map(UUID uuid) {
    return uuid == null ? null : uuid.toString();
  }

  default PasswordResetSuccessResponseDTO toSuccessResponseForEmail() {
    var dto = new PasswordResetSuccessResponseDTO();
    dto.setMessage("If the email exists, a password reset link has been sent");
    dto.setToken(null);
    dto.setTimestamp(java.time.OffsetDateTime.now(java.time.ZoneId.of("UTC")));
    return dto;
  }
}
