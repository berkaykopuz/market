package com.toyota.selling.exception;

public class SaleNotFoundException extends RuntimeException{
    public SaleNotFoundException(String message) {
        super(message);
    }
}
