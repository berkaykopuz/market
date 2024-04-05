package com.toyota.selling.exception;

public class BadProductRequestException extends RuntimeException{
    public BadProductRequestException(String message) {
        super(message);
    }
}
