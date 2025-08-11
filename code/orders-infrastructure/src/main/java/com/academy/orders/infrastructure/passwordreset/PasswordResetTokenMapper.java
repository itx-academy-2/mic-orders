package com.academy.orders.infrastructure.passwordreset;

import com.academy.orders.domain.passwordreset.entity.PasswordResetToken;
import com.academy.orders.infrastructure.passwordreset.entity.PasswordResetTokenEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PasswordResetTokenMapper {
  @Mapping(target = "id", source = "id")
  PasswordResetToken toDomain(PasswordResetTokenEntity entity);

  @Mapping(target = "id", source = "id")
  PasswordResetTokenEntity toEntity(PasswordResetToken token);
}
