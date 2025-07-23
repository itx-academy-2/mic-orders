package com.academy.orders.apirest.passwordreset.mapper;

import com.academy.orders.domain.passwordreset.dto.PasswordResetCommand;
import com.academy.orders.domain.passwordreset.dto.TokenValidationResult;
import com.academy.orders.domain.passwordreset.dto.TokenWrapper;
import com.academy.orders_api_rest.generated.model.PasswordResetRequestDTO;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class PasswordResetMapperTest {
  private PasswordResetMapper mapper;

  @BeforeEach
  void setUp() {
    mapper = Mappers.getMapper(PasswordResetMapper.class);
  }

  @Test
  void toCommand_shouldMapPasswordResetRequestDTOToCommand() {
    // Given
    var dto = new PasswordResetRequestDTO();
    UUID token = UUID.randomUUID();
    String password = "StrongPass1!";
    dto.setToken(token);
    dto.setPassword(password);

    // When
    PasswordResetCommand command = mapper.toCommand(dto);

    // Then
    assertThat(command).isNotNull();
    assertThat(command.token()).isEqualTo(token.toString());
    assertThat(command.password()).isEqualTo(password);
  }

  @Test
  void toSuccessResponseForReset_shouldMapTokenWrapperToSuccessResponse() {
    // Given
    var token = UUID.randomUUID();
    var wrapper = new TokenWrapper(token);

    // When
    var response = mapper.toSuccessResponseForReset(wrapper);

    // Then
    assertThat(response).isNotNull();
    assertThat(response.getMessage()).isEqualTo("Password has been successfully reset");
    assertThat(response.getToken()).isEqualTo(token.toString());
    assertThat(response.getTimestamp()).isNotNull();

    var now = OffsetDateTime.now(ZoneOffset.UTC);
    assertThat(response.getTimestamp()).isBeforeOrEqualTo(now.plusSeconds(5));
    assertThat(response.getTimestamp()).isAfterOrEqualTo(now.minusSeconds(5));
  }

  @Test
  void toTokenValidResponse_shouldMapTokenValidationResultToDTO() {
    // Given
    var token = UUID.randomUUID();
    var newToken = UUID.randomUUID();
    var valid = true;
    var tokenType = "PRIMARY";
    var expiresAt = Instant.now().plusSeconds(3600);
    var timestamp = Instant.now();

    var result = new TokenValidationResult(valid, token, tokenType, expiresAt, newToken, timestamp);

    // When
    var dto = mapper.toTokenValidResponse(result);

    // Then
    assertThat(dto).isNotNull();
    assertThat(dto.getValid()).isEqualTo(valid);
    assertThat(dto.getToken()).isEqualTo(token.toString());
    assertThat(dto.getNewToken()).isEqualTo(newToken.toString());
    assertThat(dto.getTokenType()).isEqualTo(tokenType);
    assertThat(dto.getExpiresAt()).isEqualTo(OffsetDateTime.ofInstant(expiresAt, ZoneOffset.UTC));
    assertThat(dto.getTimestamp()).isEqualTo(OffsetDateTime.ofInstant(timestamp, ZoneOffset.UTC));
  }

  @Test
  void toTokenValidResponse_shouldHandleNullTokens() {
    // Given
    var valid = false;
    var result = new TokenValidationResult(valid, null, null, null, null, Instant.now());

    // When
    var dto = mapper.toTokenValidResponse(result);

    // Then
    assertThat(dto).isNotNull();
    assertThat(dto.getValid()).isFalse();
    assertThat(dto.getToken()).isNull();
    assertThat(dto.getNewToken()).isNull();
    assertThat(dto.getTokenType()).isNull();
    assertThat(dto.getExpiresAt()).isNull();
    assertThat(dto.getTimestamp()).isNotNull();
  }

  @Test
  void toSuccessResponseForEmail_shouldReturnCorrectDefaultResponse() {
    // When
    var dto = mapper.toSuccessResponseForEmail();

    // Then
    assertThat(dto).isNotNull();
    assertThat(dto.getMessage()).isEqualTo("If the email exists, a password reset link has been sent");
    assertThat(dto.getToken()).isNull();
    assertThat(dto.getTimestamp()).isNotNull();

    var now = OffsetDateTime.now(ZoneOffset.UTC);
    assertThat(dto.getTimestamp()).isBeforeOrEqualTo(now.plusSeconds(5));
    assertThat(dto.getTimestamp()).isAfterOrEqualTo(now.minusSeconds(5));
  }

  @Test
  void map_shouldReturnStringRepresentationOfUUID() {
    // Given
    var uuid = UUID.randomUUID();

    // When
    var result = mapper.map(uuid);

    // Then
    assertThat(result).isEqualTo(uuid.toString());
  }

  @Test
  void map_shouldReturnNullIfUUIDIsNull() {
    // When
    var result = mapper.map(null);

    // Then
    assertThat(result).isNull();
  }
}
