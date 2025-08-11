package com.academy.orders.domain.postaddress.usecase;

import java.util.UUID;

/**
 * Use case interface for deletion user's permanent post addresses.
 */
public interface DeletePermanentPostAddressesUseCase {
    /**
     * Removes a permanent post address from the user's list of permanent post addresses.
     * A permanent post address is identified by the "permanent: " prefix in its title.
     * Instead of physically deleting the address from the database, this operation
     * updates the title to remove the "permanent: " prefix, effectively marking it as temporary.
     *
     * @param userId    the ID of the user
     * @param addressId the UUID of the post address to be removed from the permanent list
     * @author Oleksandra Bulhakova
     */
    void deletePermanentAddress(Long userId, UUID addressId);
}
