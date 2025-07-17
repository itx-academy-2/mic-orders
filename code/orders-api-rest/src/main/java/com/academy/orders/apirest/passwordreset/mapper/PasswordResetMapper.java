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

  /**
 * Converts a password reset request DTO from the API layer into a domain password reset command.
 *
 * @param dto the password reset request data from the API
 * @return the corresponding domain password reset command
 */
PasswordResetCommand toCommand(PasswordResetRequestDTO dto);

  /**
   * Maps a {@link TokenWrapper} domain object to a {@link PasswordResetSuccessResponseDTO} indicating a successful password reset.
   *
   * The response includes a fixed success message, the provided token, and the current UTC timestamp.
   *
   * @param wrapper the domain token wrapper containing the reset token
   * @return a success response DTO for password reset
   */
  @Mapping(target = "message", constant = "Password has been successfully reset")
  @Mapping(target = "token", source = "token")
  @Mapping(target = "timestamp", expression = "java(java.time.OffsetDateTime.now(java.time.ZoneId.of(\"UTC\")))")
  PasswordResetSuccessResponseDTO toSuccessResponseForReset(TokenWrapper wrapper);

  /**
   * Maps a {@link TokenValidationResult} domain object to a {@link TokenValidResponseDTO} for API responses.
   *
   * Converts token UUIDs to strings, token type, validity, expiration, and timestamp to UTC OffsetDateTime.
   *
   * @param result the token validation result to map
   * @return the corresponding token valid response DTO
   */
  @Mapping(target = "valid", source = "valid")
  @Mapping(target = "token", expression = "java(result.token().toString())")
  @Mapping(target = "newToken", expression = "java(result.newToken().toString())")
  @Mapping(target = "tokenType", source = "tokenType")
  @Mapping(target = "expiresAt", expression = "java(java.time.OffsetDateTime.ofInstant(result.expiresAt(), java.time.ZoneId.of(\"UTC\")))")
  @Mapping(target = "timestamp", expression = "java(java.time.OffsetDateTime.ofInstant(result.timestamp(), java.time.ZoneId.of(\"UTC\")))")
  TokenValidResponseDTO toTokenValidResponse(TokenValidationResult result);

  /**
   * Converts a UUID to its string representation, or returns null if the UUID is null.
   *
   * @param uuid the UUID to convert
   * @return the string representation of the UUID, or null if the input is null
   */
  default String map(UUID uuid) {
    return uuid == null ? null : uuid.toString();
  }

  /**
   * Creates a password reset success response indicating that a reset link has been sent if the email exists.
   *
   * @return a PasswordResetSuccessResponseDTO with a generic message, null token, and the current UTC timestamp
   */
  default PasswordResetSuccessResponseDTO toSuccessResponseForEmail() {
    var dto = new PasswordResetSuccessResponseDTO();
    dto.setMessage("If the email exists, a password reset link has been sent");
    dto.setToken(null);
    dto.setTimestamp(java.time.OffsetDateTime.now(java.time.ZoneId.of("UTC")));
    return dto;
  }
}
