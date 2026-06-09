package com.aitasker.exception;// BusinessException.java
public class ForbiddenException extends RuntimeException{
    public ForbiddenException(String message){
        super(message);
    }
}
