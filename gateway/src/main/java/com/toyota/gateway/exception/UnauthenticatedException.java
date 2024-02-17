package com.toyota.gateway.exception;

public class UnauthenticatedException extends RuntimeException{
    public UnauthenticatedException(String message) {
        super(message);
    }
}
