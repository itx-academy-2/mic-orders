package com.academy.orders.domain.postaddress.usecase;

import com.academy.orders.domain.postaddress.entity.PostAddressV2;

/**
 * Use case interface for creating new PostAddress.
 */
public interface CreatePostAddressUseCase {
    /**
     * Method creates new post address.
     *
     * @param postAddress {@link PostAddressV2}
     * @author Oleksandra Bulhakova
     */
    PostAddressV2 createPostAddress(PostAddressV2 postAddress);
}
