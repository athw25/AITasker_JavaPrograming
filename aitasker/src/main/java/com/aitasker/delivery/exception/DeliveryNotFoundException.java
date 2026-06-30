package com.aitasker.delivery.exception;

import com.aitasker.exception.ResourceNotFoundException;

/** Raised when a delivery cannot be found. */
public class DeliveryNotFoundException extends ResourceNotFoundException {
    public DeliveryNotFoundException(Long id) {
        super("Delivery not found with id: " + id);
    }
}
