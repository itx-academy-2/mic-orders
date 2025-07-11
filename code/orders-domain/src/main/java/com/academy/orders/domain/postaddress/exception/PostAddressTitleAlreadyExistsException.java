package com.academy.orders.domain.postaddress.exception;

public class PostAddressTitleAlreadyExistsException extends RuntimeException {
    public PostAddressTitleAlreadyExistsException(String message) {
        super(message);
    }
}
