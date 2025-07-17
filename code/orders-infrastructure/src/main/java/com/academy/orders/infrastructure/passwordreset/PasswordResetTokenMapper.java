package com.academy.orders.infrastructure.passwordreset;

import com.academy.orders.domain.passwordreset.entity.PasswordResetToken;
import com.academy.orders.infrastructure.passwordreset.entity.PasswordResetTokenEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PasswordResetTokenMapper {
  /**
   * Maps a {@link PasswordResetTokenEntity} to its corresponding domain model {@link PasswordResetToken}.
   *
   * @param entity the persistence entity representing a password reset token
   * @return the domain model representation of the password reset token
   */
  @Mapping(target = "id", source = "id")
  PasswordResetToken toDomain(PasswordResetTokenEntity entity);

  /**
   * Maps a {@link PasswordResetToken} domain object to a {@link PasswordResetTokenEntity} for persistence.
   *
   * @param token the domain object representing a password reset token
   * @return the corresponding entity object for database storage
   */
  @Mapping(target = "id", source = "id")
  PasswordResetTokenEntity toEntity(PasswordResetToken token);
}
