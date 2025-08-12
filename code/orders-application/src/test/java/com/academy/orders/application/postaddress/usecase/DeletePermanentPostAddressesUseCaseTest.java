package com.academy.orders.application.postaddress.usecase;

import com.academy.orders.domain.account.exception.AccountNotFoundException;
import com.academy.orders.domain.account.repository.AccountRepository;
import com.academy.orders.domain.postaddress.exception.PostAddressNotFoundException;
import com.academy.orders.domain.postaddress.repository.PostAddressV2Repository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
public class DeletePermanentPostAddressesUseCaseTest {
    @Mock
    private PostAddressV2Repository postAddressV2Repository;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private DeletePermanentPostAddressesUseCaseImpl useCaseImpl;

    private Long userId;

    private UUID addressId;

    @Test
    void deletePermanentAddress_Success_Test() {
        //Given
        userId = 5L;
        addressId = UUID.fromString("5b08cd5a-a34e-4bf1-aabf-f2e79740919b");

        when(accountRepository.existsById(userId)).thenReturn(true);
        when(postAddressV2Repository.checkIfAddressExistsById(addressId)).thenReturn(true);
        when(postAddressV2Repository.checkIfAddressIsPermanent(addressId)).thenReturn(true);

        doNothing().when(postAddressV2Repository).deletePermanentAddress(userId, addressId);

        //When
        useCaseImpl.deletePermanentAddress(userId, addressId);

        //Then
        verify(accountRepository).existsById(userId);
        verify(postAddressV2Repository).checkIfAddressExistsById(addressId);
        verify(postAddressV2Repository).checkIfAddressIsPermanent(addressId);
        verify(postAddressV2Repository).deletePermanentAddress(userId, addressId);
    }

    @Test
    void deletePermanentAddress_ThrowsAccountNotFoundException_Test() {
        //Given
        userId = 6L;
        addressId = UUID.fromString("5b08cd5a-a34e-4bf1-aabf-f2e79740919b");

        when(accountRepository.existsById(userId)).thenReturn(false);

        //When
        AccountNotFoundException ex = assertThrows(AccountNotFoundException.class, () -> useCaseImpl.deletePermanentAddress(userId, addressId));

        //Then
        verify(accountRepository).existsById(userId);
        verify(postAddressV2Repository, never()).checkIfAddressExistsById(addressId);
        verify(postAddressV2Repository, never()).checkIfAddressIsPermanent(addressId);
        verify(postAddressV2Repository, never()).deletePermanentAddress(userId, addressId);
        assertEquals("Account with id: 6 is not found", ex.getMessage());
    }

    @Test
    void deletePermanentAddress_ThrowsPostAddressNotFoundException_Test() {
        //Given
        userId = 5L;
        addressId = UUID.fromString("7b08cd5a-a34e-4bf1-aabf-f2e79740919b");

        when(accountRepository.existsById(userId)).thenReturn(true);
        when(postAddressV2Repository.checkIfAddressExistsById(addressId)).thenReturn(false);

        //When
        PostAddressNotFoundException ex = assertThrows(PostAddressNotFoundException.class, () -> useCaseImpl.deletePermanentAddress(userId, addressId));

        //Then
        verify(accountRepository).existsById(userId);
        verify(postAddressV2Repository).checkIfAddressExistsById(addressId);
        verify(postAddressV2Repository, never()).checkIfAddressIsPermanent(addressId);
        verify(postAddressV2Repository, never()).deletePermanentAddress(userId, addressId);
        assertEquals("PostAddress with id: 7b08cd5a-a34e-4bf1-aabf-f2e79740919b is not found", ex.getMessage());
    }

    @Test
    void deletePermanentAddress_NoChangesIfAddressIsAlreadyTemporary_Test() {
        //Given
        userId = 5L;
        addressId = UUID.fromString("8b08cd5a-a34e-4bf1-aabf-f2e79740919b");

        when(accountRepository.existsById(userId)).thenReturn(true);
        when(postAddressV2Repository.checkIfAddressExistsById(addressId)).thenReturn(true);
        when(postAddressV2Repository.checkIfAddressIsPermanent(addressId)).thenReturn(false);

        //When
        useCaseImpl.deletePermanentAddress(userId, addressId);

        //Then
        verify(accountRepository).existsById(userId);
        verify(postAddressV2Repository).checkIfAddressExistsById(addressId);
        verify(postAddressV2Repository).checkIfAddressIsPermanent(addressId);
        verify(postAddressV2Repository, never()).deletePermanentAddress(userId, addressId);
    }
}
