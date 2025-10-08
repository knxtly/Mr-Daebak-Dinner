package com.devak.mrdaebakdinner.exception;

import java.util.List;

public class InsufficientInventoryException extends RuntimeException {
    private final List<String> insufficientItems;

    public InsufficientInventoryException(String message, List<String> insufficientItems) {
        super(message);
        this.insufficientItems = insufficientItems;
    }

    public List<String> getInsufficientItems() {
        return insufficientItems;
    }
}
