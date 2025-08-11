package com.academy.orders.application.postaddress.usecase;

import com.academy.orders.domain.account.exception.AccountNotFoundException;
import com.academy.orders.domain.account.repository.AccountRepository;
import com.academy.orders.domain.postaddress.exception.PostAddressNotFoundException;
import com.academy.orders.domain.postaddress.repository.PostAddressV2Repository;
import com.academy.orders.domain.postaddress.usecase.DeletePermanentPostAddressesUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DeletePermanentPostAddressesUseCaseImpl implements DeletePermanentPostAddressesUseCase {
    private final PostAddressV2Repository postAddressV2Repository;

    private final AccountRepository accountRepository;

    @Override
    public void deletePermanentAddress(Long userId, UUID addressId) {
        if (!accountRepository.checkIfAccountExistsById(userId)) {
            throw new AccountNotFoundException(userId);
        }
        if (!postAddressV2Repository.checkIfAddressExistsById(addressId)) {
            throw new PostAddressNotFoundException(addressId);
        }
        if (!postAddressV2Repository.checkIfAddressIsPermanent(addressId)) {
            log.info("The given postAddress is already temporary - there is nothing to change");
            return;
        }
        log.info("Delete permanent post addresses with id: {} from the user's list of permanent addresses for the user with id: {}", addressId, userId);
        postAddressV2Repository.deletePermanentAddress(userId, addressId);
    }
}
