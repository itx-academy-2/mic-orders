package com.academy.orders.infrastructure.account;

import com.academy.orders.domain.accountv2.entity.AccountV2;
import com.academy.orders.infrastructure.account.entity.AccountEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AccountV2Mapper {
  AccountV2 fromEntity(AccountEntity accountEntity);
}
