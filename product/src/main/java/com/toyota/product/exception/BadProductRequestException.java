package com.toyota.product.exception;

public class BadProductRequestException extends RuntimeException{
    public BadProductRequestException(String message) {
        super(message);
    }
}
