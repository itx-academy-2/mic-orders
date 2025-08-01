package com.academy.orders.domain.postaddress.repository;

import com.academy.orders.domain.postaddress.entity.PostAddressV2;

import java.util.List;

/**
 * Repository interface for accessing and managing post addresses.
 */
public interface PostAddressV2Repository {
    /**
     * Retrieves all permanent post addresses for the given user.
     * A permanent post address is identified by the "permanent: " prefix in its title.
     *
     * @param userId the ID of the user
     * @return a list of {@link PostAddressV2} entries associated with the user that have the "permanent: " prefix in their title
     * @author Oleksandra Bulhakova
     */
    List<PostAddressV2> getPermanentPostAddressesByUserId(Long userId);
}
