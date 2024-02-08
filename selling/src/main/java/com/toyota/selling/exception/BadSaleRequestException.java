package com.toyota.selling.exception;

public class BadSaleRequestException extends RuntimeException{
    public BadSaleRequestException(String message) {
        super(message);
    }
}
