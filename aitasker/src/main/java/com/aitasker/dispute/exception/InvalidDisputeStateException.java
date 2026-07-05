package com.aitasker.dispute.exception;

import com.aitasker.exception.BusinessException;

public class InvalidDisputeStateException extends BusinessException {
    public InvalidDisputeStateException(String message) {
        super(message);
    }
}