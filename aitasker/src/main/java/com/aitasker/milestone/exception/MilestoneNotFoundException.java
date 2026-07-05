package com.aitasker.milestone.exception;

import com.aitasker.exception.ResourceNotFoundException;

/** Raised when a milestone cannot be found. */
public class MilestoneNotFoundException extends ResourceNotFoundException {
    public MilestoneNotFoundException(Long id) {
        super("Milestone not found with id: " + id);
    }
}
