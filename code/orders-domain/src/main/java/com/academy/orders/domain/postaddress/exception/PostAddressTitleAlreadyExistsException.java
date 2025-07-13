package com.academy.orders.domain.postaddress.exception;

import com.academy.orders.domain.common.exception.BadRequestException;

public class PostAddressTitleAlreadyExistsException extends BadRequestException {
    public PostAddressTitleAlreadyExistsException(String message) {
        super(message);
    }
}
