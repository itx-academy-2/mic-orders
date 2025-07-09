package com.academy.orders.infrastructure.accountV2;

import com.academy.orders.domain.accountV3.entity.AccountV3;
import com.academy.orders.infrastructure.account.entity.AccountEntity;
import com.academy.orders.infrastructure.accountV2.entity.AccountV2Entity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AccountV3Mapper {
    AccountV3 fromEntity(AccountEntity accountEntity);
}
