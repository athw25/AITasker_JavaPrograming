package com.aitasker.expert.exception;

public class ServicePackageNotFoundException extends RuntimeException {
    public ServicePackageNotFoundException(String message) {
        super(message);
    }
}