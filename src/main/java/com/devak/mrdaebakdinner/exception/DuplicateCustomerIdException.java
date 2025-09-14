package com.devak.mrdaebakdinner.exception;

public class DuplicateCustomerIdException extends RuntimeException {
    public DuplicateCustomerIdException(String message) {
        super(message);
    }
}
