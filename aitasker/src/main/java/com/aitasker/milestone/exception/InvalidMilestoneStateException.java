package com.aitasker.milestone.exception;

import com.aitasker.exception.BusinessException;

/** Raised when a milestone lifecycle transition is invalid. */
public class InvalidMilestoneStateException extends BusinessException {
    public InvalidMilestoneStateException(String message) {
        super(message);
    }
}
