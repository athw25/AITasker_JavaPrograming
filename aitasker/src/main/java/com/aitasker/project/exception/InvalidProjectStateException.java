package com.aitasker.project.exception;

import com.aitasker.exception.BusinessException;

/** Raised when a project lifecycle transition is invalid. */
public class InvalidProjectStateException extends BusinessException {
    public InvalidProjectStateException(String message) {
        super(message);
    }
}
