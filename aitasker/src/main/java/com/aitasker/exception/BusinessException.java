package com.aitasker.exception;// BusinessException.java
 public class BusinessException extends RuntimeException{
     public BusinessException(String message){
         super(message);
     }
}