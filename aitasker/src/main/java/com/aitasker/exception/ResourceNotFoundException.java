// ResourceNotFoundException.java
package com.aitasker.exception;// BusinessException.java
public class ResourceNotFoundException extends RuntimeException{
    public ResourceNotFoundException(String message){
        super(message);
    }
}