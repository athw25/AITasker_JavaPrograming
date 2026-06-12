// UnauthorizedException.java
package com.aitasker.exception;// BusinessException.java
public class UnauthorizedException extends RuntimeException{
    public UnauthorizedException(String message){
        super(message);
    }
}