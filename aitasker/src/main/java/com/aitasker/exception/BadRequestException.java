package com.aitasker.exception;// BadRequestException.java

public class BadRequestException extends RuntimeException{
    public BadRequestException(String message ){
        super(message);
    }
}