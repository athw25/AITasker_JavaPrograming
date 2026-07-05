// BusinessException.java
package com.aitasker.exception;
public class ForbiddenException extends RuntimeException{
    public ForbiddenException(String message){
        super(message);
    }
}
