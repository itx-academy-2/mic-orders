package com.academy.orders.domain.postaddress.usecase;

import com.academy.orders.domain.postaddress.entity.PostAddressV2;

import java.util.List;

/**
 * Use case interface for getting user's permanent post addresses.
 */
public interface GetPermanentPostAddressesUseCase {
    /**
     * Gets all permanent post addresses for the user.
     * A permanent post address is identified by the "permanent: " prefix in its title.
     *
     * @param userId the ID of the user
     * @return a list of {@link PostAddressV2} entries that have the "permanent: " prefix in their title and are associated with the user
     * @author Oleksandra Bulhakova
     */
    List<PostAddressV2> getPermanentPostAddressesByUserId(Long userId);
}
