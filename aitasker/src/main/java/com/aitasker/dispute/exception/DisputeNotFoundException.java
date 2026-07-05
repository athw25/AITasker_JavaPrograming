package com.aitasker.dispute.exception;

import com.aitasker.exception.ResourceNotFoundException;

public class DisputeNotFoundException extends ResourceNotFoundException {
    public DisputeNotFoundException(Long id) {
        super("Không tìm thấy Dispute với id: " + id);
    }
}