package com.academy.orders.infrastructure.passwordreset.mapper;

import com.academy.orders.domain.passwordreset.entity.PasswordResetToken;
import com.academy.orders.domain.passwordreset.entity.enumerated.TokenStatus;
import com.academy.orders.domain.passwordreset.entity.enumerated.TokenType;
import static com.academy.orders.infrastructure.ModelUtils.createSampleEntity;
import static com.academy.orders.infrastructure.ModelUtils.createSampleToken;
import com.academy.orders.infrastructure.passwordreset.PasswordResetTokenMapper;
import com.academy.orders.infrastructure.passwordreset.entity.PasswordResetTokenEntity;
import java.time.OffsetDateTime;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mapstruct.factory.Mappers.getMapper;

class PasswordResetTokenMapperTest {
    private PasswordResetTokenMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = getMapper(PasswordResetTokenMapper.class);
    }

    @Test
    void toDomain_shouldMapAllFieldsCorrectly() {
        // Given
        PasswordResetTokenEntity entity = createSampleEntity();
        // When
        var domain = mapper.toDomain(entity);

        // Then
        assertThat(domain).isNotNull();
        assertThat(domain.getId()).isEqualTo(entity.getId());
        assertThat(domain.getToken()).isEqualTo(entity.getToken());
        assertThat(domain.getAccountId()).isEqualTo(entity.getAccountId());
        assertThat(domain.getEmail()).isEqualTo(entity.getEmail());
        assertThat(domain.getType()).isEqualTo(entity.getType());
        assertThat(domain.getStatus()).isEqualTo(entity.getStatus());
        assertThat(domain.getCreatedAt()).isEqualTo(entity.getCreatedAt());
        assertThat(domain.getExpiresAt()).isEqualTo(entity.getExpiresAt());
    }

    @Test
    void toEntity_shouldMapAllFieldsCorrectly() {
        // Given
        var domain = createSampleToken();

        // When
        var entity = mapper.toEntity(domain);

        // Then
        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(domain.getId());
        assertThat(entity.getToken()).isEqualTo(domain.getToken());
        assertThat(entity.getAccountId()).isEqualTo(domain.getAccountId());
        assertThat(entity.getEmail()).isEqualTo(domain.getEmail());
        assertThat(entity.getType()).isEqualTo(domain.getType());
        assertThat(entity.getStatus()).isEqualTo(domain.getStatus());
        assertThat(entity.getCreatedAt()).isEqualTo(domain.getCreatedAt());
        assertThat(entity.getExpiresAt()).isEqualTo(domain.getExpiresAt());
    }

    @Test
    void toDomain_shouldHandleNullFieldsGracefully() {
        // Given
        var entity = new PasswordResetTokenEntity();

        // When
        var domain = mapper.toDomain(entity);

        // Then
        assertThat(domain).isNotNull();
        assertThat(domain.getId()).isNull();
        assertThat(domain.getToken()).isNull();
        assertThat(domain.getAccountId()).isNull();
        assertThat(domain.getEmail()).isNull();
        assertThat(domain.getType()).isNull();
        assertThat(domain.getStatus()).isNull();
        assertThat(domain.getCreatedAt()).isNull();
        assertThat(domain.getExpiresAt()).isNull();
    }

    @Test
    void toEntity_shouldHandleNullFieldsGracefully() {
        // Given
        var domain = PasswordResetToken.builder().build();

        // When
        var entity = mapper.toEntity(domain);

        // Then
        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isNull();
        assertThat(entity.getToken()).isNull();
        assertThat(entity.getAccountId()).isNull();
        assertThat(entity.getEmail()).isNull();
        assertThat(entity.getType()).isNull();
        assertThat(entity.getStatus()).isNull();
        assertThat(entity.getCreatedAt()).isNull();
        assertThat(entity.getExpiresAt()).isNull();
    }

    @Test
    void toDomainAndBack_shouldReturnEquivalentEntity() {
        // Given
        var originalEntity = createSampleEntity();

        // When
        var domain = mapper.toDomain(originalEntity);
        var mappedEntity = mapper.toEntity(domain);

        // Then
        assertThat(mappedEntity).usingRecursiveComparison()
            .isEqualTo(originalEntity);
    }

    @Test
    void toEntityAndBack_shouldReturnEquivalentDomain() {
        // Given
        var originalDomain = createSampleToken();

        // When
        var entity = mapper.toEntity(originalDomain);
        var mappedDomain = mapper.toDomain(entity);

        // Then
        assertThat(mappedDomain).usingRecursiveComparison()
            .isEqualTo(originalDomain);
    }

    @Test
    void toDomain_shouldMapEdgeCaseDates() {
        // Given
        var minDate = OffsetDateTime.MIN;
        var maxDate = OffsetDateTime.MAX;
        var entity = new PasswordResetTokenEntity();
        entity.setCreatedAt(minDate);
        entity.setExpiresAt(maxDate);

        // When
        var domain = mapper.toDomain(entity);

        // Then
        assertThat(domain.getCreatedAt()).isEqualTo(minDate);
        assertThat(domain.getExpiresAt()).isEqualTo(maxDate);
    }

    @Test
    void toEntity_shouldMapEdgeCaseDates() {
        // Given
        var minDate = OffsetDateTime.MIN;
        var maxDate = OffsetDateTime.MAX;
        var domain = PasswordResetToken.builder()
            .createdAt(minDate)
            .expiresAt(maxDate)
            .build();

        // When
        var entity = mapper.toEntity(domain);

        // Then
        assertThat(entity.getCreatedAt()).isEqualTo(minDate);
        assertThat(entity.getExpiresAt()).isEqualTo(maxDate);
    }

    @Test
    void toDomain_shouldMapAllEnumValues() {
        for (TokenType type : TokenType.values()) {
            var entity = new PasswordResetTokenEntity();
            entity.setType(type);
            var domain = mapper.toDomain(entity);
            assertThat(domain.getType()).isEqualTo(type);
        }
        for (TokenStatus status : TokenStatus.values()) {
            var entity = new PasswordResetTokenEntity();
            entity.setStatus(status);
            var domain = mapper.toDomain(entity);
            assertThat(domain.getStatus()).isEqualTo(status);
        }
    }

    @Test
    void toEntity_shouldMapAllEnumValues() {
        for (TokenType type : TokenType.values()) {
            var domain = PasswordResetToken.builder()
                .type(type)
                .build();
            var entity = mapper.toEntity(domain);
            assertThat(entity.getType()).isEqualTo(type);
        }
        for (TokenStatus status : TokenStatus.values()) {
            var domain = PasswordResetToken.builder()
                .status(status)
                .build();
            var entity = mapper.toEntity(domain);
            assertThat(entity.getStatus()).isEqualTo(status);
        }
    }
}
